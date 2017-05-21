package it.wknd.reactive.backend.model

case class Step(length: Float) {
  def + (other: Step): Step = Step(length + other.length)
}
