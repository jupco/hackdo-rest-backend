package com.jupco.hackdo.domain.services

trait PackageService[InventoryPackage, Box, PackageStatus, T[_], S[E] <: Seq[E]] {

  def createPackage(id: String, ownerId: String, box: Box, status: String): T[InventoryPackage]
  def getPackagesByUserId(u: String): T[S[InventoryPackage]]
  def getPackageByPackageId(id: String): T[InventoryPackage]
  def getPackagesByStatus(status: PackageStatus): T[S[InventoryPackage]]
  def updateBox(packageId: String, b: Box): T[InventoryPackage]
  def updateStatus(packageId: String, status: PackageStatus): T[InventoryPackage]
  def deletePackage(id: String): T[InventoryPackage]
}
