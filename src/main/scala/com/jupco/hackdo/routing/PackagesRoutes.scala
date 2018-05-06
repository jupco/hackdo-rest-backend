package com.jupco.hackdo.routing

import akka.http.scaladsl.model.StatusCodes.{ BadRequest, Created, OK }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.jupco.hackdo.ServiceResponse
import com.jupco.hackdo.domain.entities.{ Box, Dimension, Package, PackageStatus }
import com.jupco.hackdo.domain.services.PackageService
import com.jupco.hackdo.routing.dtos.PackageDTO
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.Encoder
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
                cp => complete(Created    -> cp)
              )
              .runAsync
          ) {
            case Success(route) => route
            case Failure(_)     => complete(BadRequest)
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
            cp => complete(OK         -> cp)
          )
          .runAsync
      ) {
        case Success(route) => route
        case Failure(_)     => complete(BadRequest)
      }
    }
  }

  implicit def encoderDimension: Encoder[Dimension] = Encoder.forProduct1("dimension")(_.value)
}
