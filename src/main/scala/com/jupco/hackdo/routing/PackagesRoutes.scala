package com.jupco.hackdo.routing

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes.Created
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.jupco.hackdo.ServiceResponse
import com.jupco.hackdo.domain.entities.{ Box, Package, PackageStatus }
import com.jupco.hackdo.domain.services.PackageService
import com.jupco.hackdo.routing.dtos.{ CreatePackageRequest, PackageDTO }
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.generic.auto._

trait PackagesRoutes extends ErrorAccumulatingCirceSupport with Transformations with RoutesTransformation {

  def packageService: PackageService[Package, Box, PackageStatus, ServiceResponse, List]

  def packagesRoutes: Route = createPackage ~ getPackage ~ getAllPackagesByStatus

  def createPackage: Route = path("package") {
    post {
      entity(as[CreatePackageRequest]) { p =>
        transformDirective(p.box) { box: Box =>
          val r = packageService.createPackage(UUID.randomUUID.toString, p.ownerId, box, p.status)
          onCompleteEitherT(r)(_.safeTransform[PackageDTO], statusCodeSuccess = Created)
        }
      }
    }
  }

  def getPackage: Route = path("package" / Segment) { id =>
    get {
      onCompleteEitherT(packageService.getPackageByPackageId(id))(_.safeTransform[PackageDTO])
    }
  }

  def getAllPackagesByStatus: Route = path("packages" / Segment) { status =>
    get {
      transformDirective(status) { st: PackageStatus =>
        onCompleteEitherT(packageService.getPackagesByStatus(st))(_.map(_.safeTransform[PackageDTO]))
      }
    }
  }
}
