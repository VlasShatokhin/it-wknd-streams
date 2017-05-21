package it.wknd.reactive.backend.model

import scala.concurrent.duration.Duration

case class AggregatedSteps(count: Int, length: Float, period: Duration) {
  def + (step: Step): AggregatedSteps = AggregatedSteps(count + 1, length + step.length, period)
}
