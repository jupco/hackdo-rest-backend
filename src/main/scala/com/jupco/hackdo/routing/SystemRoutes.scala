package com.jupco.hackdo.routing

import java.time.ZonedDateTime

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.circe.generic.auto._
import io.circe.java8.time._

trait SystemRoutes extends PackagesRoutes {

  protected def now = ZonedDateTime.now

  def routes: Route = healthRoute ~ packagesRoutes

  def healthRoute: Route = path("health") {
    get {
      complete(OK -> HealthResponse(now))
    }
  }

  case class HealthResponse(time: ZonedDateTime)
}
