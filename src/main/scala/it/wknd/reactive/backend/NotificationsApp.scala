package it.wknd.reactive.backend

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import com.softwaremill.macwire.wire
import com.typesafe.config.ConfigFactory
import it.wknd.reactive.backend.flow.EventGraph
import it.wknd.reactive.backend.model.{HealthNotification, HeartRate, Step}
import it.wknd.reactive.backend.source.{HrActorSource, SourceProvider, StepActorSource}

import scala.concurrent.Future

object NotificationsApp extends App {

  implicit val config = ConfigFactory.load()
  implicit val actorSystem = ActorSystem("hr-backend")
  implicit val ec = actorSystem.dispatcher
  implicit val materializer = ActorMaterializer()

  lazy val sourceProvider = wire[SourceProvider]

  val hrActor = actorSystem.actorOf(Props[HrActorSource])
  val hrPub = ActorPublisher[HeartRate](hrActor)

  val stepActor = actorSystem.actorOf(Props[StepActorSource])
  val stepPub = ActorPublisher[Step](stepActor)

  RunnableGraph fromGraph {
    EventGraph(
      stepSource = Source.fromPublisher(stepPub),
      hrSource = Source.fromPublisher(hrPub),
      sink = Sink.actorSubscriber[HealthNotification](Props[NotifierActor]))
  } run()

  val bindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(sourceProvider.routes(hrActor = hrActor, stepActor = stepActor), "localhost", 2525)
}
