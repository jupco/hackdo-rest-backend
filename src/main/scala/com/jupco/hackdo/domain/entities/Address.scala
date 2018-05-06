package com.jupco.hackdo.domain.entities

import cats.data.Validated
import cats.syntax.validated._

case class Address(
    primarySegmentType: SegmentType,
    firstField: String,
    secondarySegmentType: SegmentType,
    secondField: String
)

object Address {
  def apply(
      primarySegmentType: String,
      firstField: String,
      secondarySegmentType: String,
      secondField: String
  ): Either[ServiceError, Address] = {
    import cats.syntax.apply._
    import cats.instances.either._
    (SegmentType(primarySegmentType).toEither, SegmentType(secondarySegmentType).toEither)
      .mapN((a: SegmentType, c: SegmentType) => new Address(a, firstField, c, secondField))
  }
}

sealed trait SegmentType
case object Street extends SegmentType
case object Avenue extends SegmentType

object SegmentType {
  def apply(string: String): Validated[ServiceError, SegmentType] = string match {
    case "Street" | "St" => Street.valid
    case "Avenue" | "Av" => Avenue.valid
    case v               => InvalidAddressSegmentType(message = s"$v is not a valid value for the address segment type").invalid
  }
}
