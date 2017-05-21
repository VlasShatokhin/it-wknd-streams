package it.wknd.reactive.backend.source

import akka.actor.Actor
import akka.stream.actor.ActorPublisher
import it.wknd.reactive.backend.model.HeartRate

class HrActorSource extends Actor with ActorPublisher[HeartRate] {
  import akka.stream.actor.ActorPublisherMessage._

  var items: List[HeartRate] = List.empty

  def receive: Receive = {
    case event: HeartRate =>
      if (totalDemand == 0) items = items :+ event
      else onNext(event)

    case Request(demand) =>
      if (demand > items.size) {
        items foreach onNext
        items = List.empty
      } else {
        val (send, keep) = items.splitAt(demand.toInt)
        items = keep
        send foreach onNext
      }

    case other =>
      println(s"got other $other")
  }
}
