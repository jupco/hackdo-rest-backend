package com.jupco.hackdo.routing

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jupco.hackdo.TestSpec

class SystemRoutesTests extends TestSpec with ScalatestRouteTest with SystemRoutes {

  "The Inventory service" should {

    "return a healthy response for GET requests to the health path" in {
      Get("/health") ~> routes ~> check {
        responseAs[String] shouldEqual "healthy"
      }
    }
  }
}
