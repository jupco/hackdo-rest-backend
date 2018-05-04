package com.jupco.hackdo.domain.entities

trait ServiceError {
  def code: String
  def message: String
}

case class InvalidAddressSegmentType(code: String = "001", message: String) extends ServiceError
case class InvalidBoxDimension(code: String = "002", message: String)       extends ServiceError
case class InvalidPackageStatus(code: String = "003", message: String) extends ServiceError
