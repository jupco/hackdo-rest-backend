package com.jupco.hackdo.domain.entities

import cats.data.Validated
import cats.data.Validated.{ Invalid, Valid }
import com.jupco.hackdo.domain.entities.Box.Kilos

case class Box(length: Dimension, width: Dimension, height: Dimension, weight: Kilos) {
  def volume: Volume = length * width * height
}

object Box {
  type Kilos = Double
}

case class Dimension(value: Double) {
  def *(anotherDimension: Dimension): Area = Area(value * anotherDimension.value)
}

object Dimension {
  def apply(value: Double): Validated[ServiceError, Dimension] =
    if (value > 0) Valid(new Dimension(value))
    else Invalid(InvalidBoxDimension(message = s"$value is not a valid value for a box dimension"))
}

case class Area(value: Double) {
  def *(dimension: Dimension): Volume = Volume(value * dimension.value)
}
case class Volume(value: Double)
