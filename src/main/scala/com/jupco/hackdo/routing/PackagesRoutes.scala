package com.jupco.hackdo.routing

import akka.http.scaladsl.model.StatusCodes.{ BadRequest, Created, InternalServerError, OK }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.jupco.hackdo.ServiceResponse
import com.jupco.hackdo.domain.entities.{ Box, Package, PackageStatus }
import com.jupco.hackdo.domain.services.PackageService
import com.jupco.hackdo.routing.dtos.PackageDTO
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.generic.auto._
import monix.execution.Scheduler

import scala.util.{ Failure, Success }

trait PackagesRoutes extends ErrorAccumulatingCirceSupport with Transformations {

  implicit val sc: Scheduler = Scheduler.io()
  def packageService: PackageService[Package, Box, PackageStatus, ServiceResponse, List]

  def packagesRoutes: Route = createPackage ~ getPackage

  def createPackage: Route = path("package") {
    post {
      entity(as[PackageDTO]) { p =>
        transformDirective(p) { pa: Package =>
          val r = packageService.createPackage(pa)
          onComplete(
            r.fold(
                se => complete(BadRequest -> se),
                cp => complete(Created    -> cp.safeTransform[PackageDTO])
              )
              .runAsync
          ) {
            case Success(route) => route
            case Failure(exception) =>
              logger.error(exception.getMessage)
              exception.getStackTrace.foreach(e => logger.debug(e.toString))
              complete(InternalServerError)
          }
        }
      }
    }
  }

  def getPackage: Route = path("package" / Segment) { id =>
    get {
      val r = packageService.getPackageByPackageId(id)
      onComplete(
        r.fold(
            se => complete(BadRequest -> se),
            cp => complete(OK         -> cp.safeTransform[PackageDTO])
          )
          .runAsync
      ) {
        case Success(route) => route
        case Failure(_)     => complete(BadRequest)
      }
    }
  }
}
