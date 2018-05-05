package com.jupco.hackdo.domain.services.impl

import cats.data.{ EitherT, Validated }
import cats.syntax.validated._
import com.jupco.hackdo._
import com.jupco.hackdo.domain.PackagesEnvironment
import com.jupco.hackdo.domain.entities.{ Box, BoxSizeTooBigToStore, InvalidUserId, Package, PackageNotFound, PackageStatus, ServiceError, User, Volume }
import com.jupco.hackdo.domain.services.PackageService
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task

class PackageServiceImpl(env: PackagesEnvironment[Package, User, PackageStatus, Task, List])
    extends PackageService[Package, Box, PackageStatus, ServiceResponse, List] with LazyLogging {

  override def createPackage(p: Package): ServiceResponse[Package] = {
    logger.info(s"Creating a new Package with id ${p.id}")
    for {
      _ <- EitherT.fromOptionF(
        env.usersClient.getUserById(p.owner.id),
        InvalidUserId(message = s"the user ${p.owner.id} doesn't exist")
      )
      _            <- EitherT.fromEither[Task](validateBoxSize(p.box).toEither)
      packageSaved <- EitherT.right(env.packagesRepository.storeOrUpdate(p))
    } yield packageSaved
  }

  override def getPackagesByUserId(u: String): ServiceResponse[List[Package]] = {
    logger.info(s"Retrieving packages for user with id $u")
    for {
      _ <- EitherT.fromOptionF(
        env.usersClient.getUserById(u),
        InvalidUserId(message = s"the user ${u} doesn't exist")
      )
      pks <- EitherT.right(env.packagesRepository.getByUserId(u))
    } yield pks
  }

  override def getPackageByPackageId(id: String): ServiceResponse[Package] = {
    logger.info(s"Retrieving a package by its id: $id")
    EitherT.fromOptionF[Task, ServiceError, Package](
      env.packagesRepository.getByPackageId(id),
      PackageNotFound(message = s"the package with id $id wasn't found in the system")
    )
  }

  override def getPackagesByStatus(status: PackageStatus): ServiceResponse[List[Package]] = {
    logger.info(s"Retrieving packages by their status: ${status.toString}")
    EitherT
      .right[ServiceError](env.packagesRepository.getByStatus(status))
      .leftMap(_ => PackageNotFound(message = "packages could not be found"))
  }

  override def updateBox(packageId: String, b: Box): ServiceResponse[Package] = {
    logger.info(s"Updating box for a package with id $packageId, assigning new box $b")
    for {
      p <- EitherT.fromOptionF(
        env.packagesRepository.getByPackageId(packageId),
        PackageNotFound(message = s"the package with id $packageId wasn't found in the system")
      )
      np = p.copy(box = b)
      _  <- EitherT.fromEither[Task](validateBoxSize(b).toEither)
      up <- EitherT.right(env.packagesRepository.storeOrUpdate(np))
    } yield up
  }

  override def updateStatus(packageId: String, status: PackageStatus): ServiceResponse[Package] = {
    logger.info(s"Updating status for a package with id $packageId, assigning new status $status")
    for {
      p <- EitherT.fromOptionF(
        env.packagesRepository.getByPackageId(packageId),
        PackageNotFound(message = s"the package with id $packageId wasn't found in the system")
      )
      np = p.copy(status = status)
      up <- EitherT.right(env.packagesRepository.storeOrUpdate(np))
    } yield up
  }

  override def deletePackage(p: Package): ServiceResponse[Package] = {
    logger.info(s"Deleting the package with id ${p.id}")
    EitherT
      .right[ServiceError](env.packagesRepository.delete(p))
      .leftMap(_ => PackageNotFound(message = "packages could not be found"))
  }

  private def validateBoxSize(box: Box): Validated[ServiceError, Box] =
    if (box.volume < Volume(env.config.inventoryConfiguration.maxVolumePerBox)) box.valid
    else
      BoxSizeTooBigToStore(message = "This Box exceeds the maximum volume which this storage is able to store").invalid
}
