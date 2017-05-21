package it.wknd.reactive.backend.source

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes.NoContent
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import it.wknd.reactive.backend.model.{HeartRate, Step}

import scala.language.postfixOps

class SourceProvider(implicit actorSystem: ActorSystem, materializer: ActorMaterializer) {

  import it.wknd.reactive.JsonProtocol._

  def routes(hrActor: ActorRef, stepActor: ActorRef): Route =
    path("hr") {
      post {
        pathEndOrSingleSlash {
          entity(as[HeartRate]) { hrEvent =>
            hrActor ! hrEvent
            complete(NoContent)
          }
        }
      }
    } ~ path("step") {
      post {
        pathEndOrSingleSlash {
          entity(as[Step]) { step =>
            stepActor ! step
            complete(NoContent)
          }
        }
      }
    }
}
