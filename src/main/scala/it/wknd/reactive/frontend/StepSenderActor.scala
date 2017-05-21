package it.wknd.reactive.frontend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.actor.ActorSubscriberMessage.OnNext
import akka.stream.actor.{ActorSubscriber, MaxInFlightRequestStrategy}
import it.wknd.reactive.Logging

import scala.concurrent.ExecutionContext

class StepSenderActor(implicit val actorSystem: ActorSystem,
                      val ec: ExecutionContext,
                      val materializer: ActorMaterializer) extends ActorSubscriber with Logging {
  private var inFlight = 0
  private val http = Http()

  override protected def requestStrategy = new MaxInFlightRequestStrategy(10) {
    override def inFlightInternally: Int = inFlight
  }

  def receive: Receive = {
    case OnNext(event: String) =>
      inFlight += 1

      http.singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = Uri("http://localhost:2525/step"),
        entity = HttpEntity(
          ContentTypes.`application/json`,
          event
        ))) onComplete {
        tryy =>
          log.info(tryy.toString)
          inFlight -= 1
      }

  }
}
