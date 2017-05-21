package it.wknd

import akka.actor.Actor
import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import it.wknd.reactive.backend.model.{HeartRate, Step}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

package object reactive {

  trait Logging extends Actor {
    val log = Logging(context.system, this)
  }

  object JsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val heartRate: RootJsonFormat[HeartRate] = jsonFormat1(HeartRate)
    implicit val step: RootJsonFormat[Step] = jsonFormat1(Step)
  }

}
