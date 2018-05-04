package com.jupco.hackdo.domain.entities

import cats.data.Validated
import cats.syntax.validated._

sealed trait PackageStatus
case object Transit   extends PackageStatus
case object Received  extends PackageStatus
case object Delivered extends PackageStatus
case object Review    extends PackageStatus

object PackageStatus {
  def apply(string: String): Validated[ServiceError, PackageStatus] = string.toLowerCase.trim match {
    case "transit"   => Transit.valid
    case "received"  => Received.valid
    case "delivered" => Delivered.valid
    case "review"    => Review.valid
    case v           => InvalidPackageStatus(message = s"$v is not a valid package status").invalid
  }
}
