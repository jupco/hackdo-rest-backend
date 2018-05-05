package com.jupco.hackdo.routing.dtos

case class PackageDTO(id: String, owner: UserDTO, box: BoxDTO, status: String)

case class UserDTO(id: String, name: String, lastName: String, address: AddressDTO, telephone: String)

case class AddressDTO(primarySegmentType: String, firstField: String, secondarySegmentType: String, secondField: String)

case class BoxDTO(length: Double, width: Double, height: Double, weight: Double)
