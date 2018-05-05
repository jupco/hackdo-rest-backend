package com.jupco.hackdo.routing

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes.Created
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import dtos.PackageDTO
import io.circe.generic.auto._

trait PackagesRoutes extends ErrorAccumulatingCirceSupport {

  def packagesRoutes: Route = createPackage

  def createPackage: Route = path("package") {
    post {
      entity(as[PackageDTO]) { p =>
        complete(Created -> p)
      }
    }
  }
}
