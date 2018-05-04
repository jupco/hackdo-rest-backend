package com.jupco.hackdo.domain.entities

import cats.data.Validated
import cats.data.Validated.{ Invalid, Valid }

case class Address(
    primarySegmentType: SegmentType,
    firstField: String,
    secondarySegmentType: SegmentType,
    secondField: String
)

sealed trait SegmentType
case object Street extends SegmentType
case object Avenue extends SegmentType

object SegmentType {
  def apply(string: String): Validated[ServiceError, SegmentType] = string match {
    case "Street" | "St" => Valid(Street)
    case "Avenue" | "Av" => Valid(Avenue)
    case v               => Invalid(InvalidAddressSegmentType(message = s"$v is not a valid value for the address segment type"))
  }
}
