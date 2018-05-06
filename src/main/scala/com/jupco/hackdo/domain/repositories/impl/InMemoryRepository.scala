package com.jupco.hackdo.domain.repositories.impl

import com.jupco.hackdo.domain.entities.{ Package, PackageStatus }
import com.jupco.hackdo.domain.repositories.PackagesRepository
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task

import scala.collection.mutable

class InMemoryRepository extends PackagesRepository[Package, PackageStatus, Task, List] with LazyLogging {

  var db: mutable.Map[String, Package] = mutable.Map.empty[String, Package]

  override def storeOrUpdate(p: Package): Task[Package] = Task {
    logger.info(s"storing in $db")
    db += p.id -> p
    reportDbStatus()
    p
  }

  private def reportDbStatus() = {
    logger.info("Reporting status")
    db.foreach {
      case (st, pa) =>
        logger.info(s"record $st - $pa")
    }
    logger.info("ends reporting status")
  }

  override def getByPackageId(id: String): Task[Option[Package]] = Task {
    logger.info(s"getting from $db")
    reportDbStatus()
    db.get(id)
  }

  override def getByUserId(userId: String): Task[List[Package]] = Task {
    reportDbStatus()
    db.values.filter(_.owner.id == userId).toList
  }

  override def getByStatus(status: PackageStatus): Task[List[Package]] = Task {
    reportDbStatus()
    db.values.filter(_.status == status).toList
  }

  override def delete(p: Package): Task[Package] = Task {
    reportDbStatus()
    db.remove(p.id)
    p
  }
}
