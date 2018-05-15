package com.jupco.hackdo.routing

import java.time.ZonedDateTime

import akka.http.javadsl.model.headers.HttpOriginRange
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.headers.HttpOrigin
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._
import io.circe.java8.time._

trait SystemRoutes extends PackagesRoutes with LazyLogging {

  protected def now = ZonedDateTime.now

  def routes: Route =
    cors(CorsSettings.defaultSettings.withAllowedOrigins(HttpOriginRange.create(HttpOrigin("http://localhost:3000")))) {
      extractRequest { req =>
        logger.info(req.toString)
        healthRoute ~ packagesRoutes
      }
    }

  def healthRoute: Route = path("health") {
    get {
      complete(OK -> HealthResponse(now))
    }
  }
}

case class HealthResponse(time: ZonedDateTime)
