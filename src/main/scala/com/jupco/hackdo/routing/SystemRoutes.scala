package com.jupco.hackdo.routing

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.server.Directives.{ complete, get, path }
import akka.http.scaladsl.server.Route

trait SystemRoutes {

  def routes: Route = healthRoute

  def healthRoute: Route = path("health") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "healthy"))
    }
  }
}
