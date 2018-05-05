package com.jupco.hackdo.domain.repositories

trait PackagesRepository[InventoryPackage, PackageStatus, T[_], S[E] <: Seq[E]] {

  def storeOrUpdate(p: InventoryPackage): T[InventoryPackage]
  def getByPackageId(id: String): T[Option[InventoryPackage]]
  def getByUserId(userId: String): T[S[InventoryPackage]]
  def getByStatus(status: PackageStatus): T[S[InventoryPackage]]
  def delete(p: InventoryPackage): T[InventoryPackage]

}
