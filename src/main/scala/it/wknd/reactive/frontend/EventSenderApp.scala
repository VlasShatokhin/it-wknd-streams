package it.wknd.reactive.frontend

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.common.EntityStreamingSupport
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.duration.Duration

object EventSenderApp extends App {

  implicit val actorSystem = ActorSystem("hr-frontend")
  implicit val materializer = ActorMaterializer()
  implicit val ec = actorSystem.dispatcher

  implicit val jsonStreamingSupport = EntityStreamingSupport.json()

  val hrData =
    """{"rate": 60}
      |{"rate": 60}
      |{"rate": 122}
      |{"rate": 50}
      |{"rate": 50}
      |{"rate": 80}
      |{"rate": 120}
      |{"rate": 140}
      |{"rate": 140}
      |{"rate": 140}
      |{"rate": 133}
      |{"rate": 128}
      |{"rate": 120}
      |{"rate": 128}
      |{"rate": 122}
      |{"rate": 101}
      |{"rate": 110}
      |{"rate": 95}
      |{"rate": 90}
      |{"rate": 85}
      |{"rate": 80}
      |{"rate": 75}
      |{"rate": 95}
      |{"rate": 120}
      |{"rate": 135}
      |{"rate": 160}
      |{"rate": 160}
      |{"rate": 195}
      |{"rate": 90}
      |{"rate": 85}""".stripMargin.lines

  val stepData =
    """{"length": 1.1}
      |{"length": 1.0}
      |{"length": 0.8}
      |{"length": 1.0}
      |{"length": 2.0}
      |{"length": 1.5}
      |{"length": 1.5}
      |{"length": 1.5}
      |{"length": 1.4}
      |{"length": 1.7}
      |{"length": 1.4}
      |{"length": 1}
      |{"length": 0.8}
      |{"length": 0.8}
      |{"length": 0.8}
      |{"length": 0.8}
      |{"length": 0}
      |{"length": 0}
      |{"length": 0}
      |{"length": 0.8}
      |{"length": 0.8}
      |{"length": 0.8}
      |{"length": 0.7}
      |{"length": 0.6}
      |{"length": 0.5}
      |{"length": 0}
      |{"length": 0}
      |{"length": 0}
      |{"length": 0}
      |{"length": 0}""".stripMargin.lines

  val config = ConfigFactory.load()
  val normalizedRate = Duration fromNanos {
    config.getDuration("notifications.normalized-rate").toNanos
  }

  Source.fromIterator(() => hrData)
    .throttle(2, 3 seconds, 1, ThrottleMode.shaping)
    .runWith(Sink.actorSubscriber[Any](Props(new HrSenderActor)))

  Source.fromIterator(() => stepData)
    .throttle(3, 4 seconds, 1, ThrottleMode.shaping)
    .runWith(Sink.actorSubscriber[Any](Props(new StepSenderActor)))
}
