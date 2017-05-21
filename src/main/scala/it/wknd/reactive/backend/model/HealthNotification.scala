package it.wknd.reactive.backend.model

object HealthNotification {

  case class Green(steps: AggregatedSteps,
                   heartRate: Int) extends HealthNotification

  case class Yellow(message: String,
                    steps: AggregatedSteps,
                    heartRate: Int) extends HealthNotification

  case class Red(message: String,
                 steps: AggregatedSteps,
                 heartRate: Int) extends HealthNotification
}

trait HealthNotification {

  def steps: AggregatedSteps

  def heartRate: Int
}
