package com.jupco.hackdo.domain.entities

case class Package(id: String, owner: User, box: Box, status: PackageStatus)
