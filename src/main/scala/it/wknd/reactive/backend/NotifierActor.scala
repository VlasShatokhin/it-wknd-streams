package it.wknd.reactive.backend

import akka.stream.actor.ActorSubscriberMessage.OnNext
import akka.stream.actor.{ActorSubscriber, MaxInFlightRequestStrategy}
import it.wknd.reactive.Logging
import it.wknd.reactive.backend.model.HealthNotification

class NotifierActor extends ActorSubscriber with Logging {
  private var worstHeartRate: Option[Int] = None
  private var inFlight = 0

  override protected def requestStrategy = new MaxInFlightRequestStrategy(10) {
    override def inFlightInternally: Int = inFlight
  }

  def receive: Receive = {
    case OnNext(event: HealthNotification) =>
      inFlight += 1
      processEvent(event)
      log.info(event.toString)
      inFlight -= 1
  }

  def processEvent(decision: HealthNotification): Unit =
    worstHeartRate = Some((decision.heartRate :: worstHeartRate.toList).max)
}
