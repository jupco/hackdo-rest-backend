package com.jupco.hackdo.domain

import com.jupco.hackdo.domain.repositories.PackagesRepository
import com.jupco.hackdo.infrastructure.clients.UsersClient
import com.jupco.hackdo.infrastructure.configuration.ConfigApp

trait PackagesEnvironment[InventoryPackage, User, PackageStatus, T[_], S[E] <: Seq[E]] {

  def packagesRepository: PackagesRepository[InventoryPackage, PackageStatus, T, S]
  def usersClient: UsersClient[User, T]
  def config: ConfigApp

}
