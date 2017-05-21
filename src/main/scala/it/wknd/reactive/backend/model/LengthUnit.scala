package it.wknd.reactive.backend.model

object LengthUnit {

  final object Meter extends LengthUnit
  final object Cm extends LengthUnit
}

sealed trait LengthUnit
