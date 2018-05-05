package com.jupco.hackdo.routing

import java.time.ZonedDateTime

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jupco.hackdo.TestSpec
import io.circe.generic.auto._
import io.circe.java8.time._

class SystemRoutesTests extends TestSpec with ScalatestRouteTest with SystemRoutes {

  override def now: ZonedDateTime = ZonedDateTime.parse("2018-05-05T10:15:30-05:00")

  "The Inventory service" should {

    "return a healthy response for GET requests to the health path" in {
      Get("/health") ~> routes ~> check {
        responseAs[HealthResponse].time shouldBe now
      }
    }
  }
}
