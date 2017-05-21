package it.wknd.reactive.backend.flow

import akka.NotUsed
import akka.actor.ActorRef
import akka.stream._
import akka.stream.scaladsl.{Flow, GraphDSL, Sink, Source, Zip}
import com.typesafe.config.Config
import it.wknd.reactive.backend.model.HealthNotification.{Green, Red, Yellow}
import it.wknd.reactive.backend.model.{AggregatedSteps, HealthNotification, HeartRate, Step}

import scala.concurrent.duration.{Duration, _}
import scala.language.postfixOps

object EventGraph {

  def apply(stepSource: Source[Step, NotUsed],
            hrSource: Source[HeartRate, NotUsed],
            sink: Sink[HealthNotification, ActorRef])(implicit config: Config): Graph[ClosedShape, NotUsed] = {

    val normalizedRate = Duration fromNanos {
      config.getDuration("notifications.normalized-rate").toNanos
    }

    //def toPerMinute(rate: Int): Int =
      //rate * ((1 minute) / normalizedRate).toInt

    GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val groupSteps = b add {
        Flow[Step].groupedWithin(Int.MaxValue, normalizedRate)
      }

      val groupHR = b add {
        Flow[HeartRate].map(_ rate).filter(_ > 0)
          .groupedWithin(Int.MaxValue, normalizedRate)
      }

      val aggSteps = b add {
        Flow[Seq[Step]] map {
          _.foldLeft(AggregatedSteps(0, 0f, normalizedRate))(_ + _)
        }
      }

      val avgHR = b add {
        Flow[Seq[Int]].map(seq => seq.sum / seq.size)
          //.map(toPerMinute)
      }

      val zip = b add {
        Zip[Int, AggregatedSteps]
      }

      val diagnoseFlow = b add {
        diagnoseGenerator
      }

      hrSource    ~> groupHR    ~> avgHR    ~>  zip.in0
      stepSource  ~> groupSteps ~> aggSteps ~>  zip.in1
                                                zip.out ~> diagnoseFlow ~> sink

      ClosedShape
    }
  }

  private def diagnoseGenerator = Flow.fromFunction[(Int, AggregatedSteps), HealthNotification] {
    case (heart, step) if step.length > 10 && heart > 120 => Yellow("Slow down.", step, heart)
    case (heart, step) if step.length > 1 && step.length <= 10 && heart > 120 => Yellow("You probably should stop.", step, heart)
    case (heart, step) if step.length == 0 && heart > 120 => Red("Get a doctor now!", step, heart)
    case (heart, step) => Green(step, heart)
  }
}
