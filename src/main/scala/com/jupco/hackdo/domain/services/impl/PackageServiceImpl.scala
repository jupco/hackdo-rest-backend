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

  override def createPackage(pid: String, ownerId: String, b: Box, status: String): ServiceResponse[Package] = {
    logger.info(s"Creating a new Package with id $pid")
    for {
      u <- EitherT.fromOptionF(
        env.usersClient.getUserById(ownerId),
        InvalidUserId(message = s"the user '$ownerId' doesn't exist")
      )
      _  <- EitherT.fromEither[Task](validateBoxSize(b).toEither)
      st <- EitherT.fromEither[Task](PackageStatus(status).toEither)
      pck = Package(id = pid, owner = u, box = b, status = st)
      packageSaved <- EitherT.right(env.packagesRepository.storeOrUpdate(pck))
    } yield packageSaved
  }

  override def getPackagesByUserId(u: String): ServiceResponse[List[Package]] = {
    logger.info(s"Retrieving packages for user with id $u")
    for {
      _ <- EitherT.fromOptionF(
        env.usersClient.getUserById(u),
        InvalidUserId(message = s"the user '$u' doesn't exist")
      )
      pks <- EitherT.right(env.packagesRepository.getByUserId(u))
    } yield pks
  }

  override def getPackageByPackageId(id: String): ServiceResponse[Package] = {
    logger.info(s"Retrieving a package by its id: $id")
    EitherT.fromOptionF[Task, ServiceError, Package](
      env.packagesRepository.getByPackageId(id),
      PackageNotFound(message = s"the package with id '$id' wasn't found in the system")
    )
  }

  override def getPackagesByStatus(status: PackageStatus): ServiceResponse[List[Package]] = {
    logger.info(s"Retrieving packages by their status: '${status.toString}'")
    EitherT
      .right[ServiceError](env.packagesRepository.getByStatus(status))
      .leftMap(_ => PackageNotFound(message = "packages could not be found"))
  }

  override def updateBox(packageId: String, b: Box): ServiceResponse[Package] = {
    logger.info(s"Updating box for a package with id '$packageId', assigning new box '$b'")
    for {
      p <- EitherT.fromOptionF(
        env.packagesRepository.getByPackageId(packageId),
        PackageNotFound(message = s"the package with id '$packageId' wasn't found in the system")
      )
      np = p.copy(box = b)
      _  <- EitherT.fromEither[Task](validateBoxSize(b).toEither)
      up <- EitherT.right(env.packagesRepository.storeOrUpdate(np))
    } yield up
  }

  override def updateStatus(packageId: String, status: PackageStatus): ServiceResponse[Package] = {
    logger.info(s"Updating status for a package with id '$packageId', assigning new status $status")
    for {
      p <- EitherT.fromOptionF(
        env.packagesRepository.getByPackageId(packageId),
        PackageNotFound(message = s"the package with id '$packageId' wasn't found in the system")
      )
      np = p.copy(status = status)
      up <- EitherT.right(env.packagesRepository.storeOrUpdate(np))
    } yield up
  }

  override def deletePackage(id: String): ServiceResponse[Package] = {
    logger.info(s"Deleting the package with id $id")
    for {
      p <- EitherT.fromOptionF(
        env.packagesRepository.getByPackageId(id),
        PackageNotFound(message = s"the package with id '$id' wasn't found in the system")
      )
      d <- EitherT.right(env.packagesRepository.delete(p))
    } yield d
  }

  private def validateBoxSize(box: Box): Validated[ServiceError, Box] =
    if (box.volume < Volume(env.config.inventoryConfiguration.maxVolumePerBox)) box.valid
    else
      BoxSizeTooBigToStore(message = "This Box exceeds the maximum volume which this storage is able to store").invalid
}
