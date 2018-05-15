package com.jupco.hackdo.routing

import java.time.ZonedDateTime

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.testkit.ScalatestRouteTest
import cats.data.EitherT
import com.jupco.hackdo.{ ServiceResponse, TestSpec }
import com.jupco.hackdo.domain.entities
import com.jupco.hackdo.domain.entities.{ Address, Avenue, Box, Package, PackageStatus, ServiceError, Street, Transit, User }
import com.jupco.hackdo.domain.services.PackageService
import com.jupco.hackdo.routing.dtos.PackageDTO
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.generic.auto._
import io.circe.java8.time._
import monix.eval.Task

class SystemRoutesTests
    extends TestSpec with ScalatestRouteTest with ErrorAccumulatingCirceSupport with Transformations {

  object Router extends SystemRoutes {
    override def now: ZonedDateTime = ZonedDateTime.parse("2018-05-05T10:15:30-05:00")
    override val packageService = new PackageService[entities.Package, Box, PackageStatus, ServiceResponse, List] {
      override def createPackage(p: Package): ServiceResponse[Package] =
        EitherT.right[ServiceError](Task.now(`package`))

      override def getPackagesByUserId(u: String): ServiceResponse[List[Package]] = ???

      override def getPackageByPackageId(id: String): ServiceResponse[Package] = ???

      override def getPackagesByStatus(status: PackageStatus): ServiceResponse[List[Package]] = ???

      override def updateBox(packageId: String, b: Box): ServiceResponse[Package] = ???

      override def updateStatus(packageId: String, status: PackageStatus): ServiceResponse[Package] = ???

      override def deletePackage(p: Package): ServiceResponse[Package] = ???
    }

    val `package` = Package(
      id = "1234567890",
      owner = User(
        id = "1234",
        name = "John",
        lastName = "Doe",
        address = Address(
          primarySegmentType = Street,
          firstField = "42",
          secondarySegmentType = Avenue,
          secondField = "32-09"
        ),
        telephone = "3214567"
      ),
      box = Box(
        length = 1D,
        width = 5D,
        height = 2D,
        weight = 2D
      ).right.get,
      status = Transit
    )
  }

  "The Inventory service" should {

    "return a healthy response for GET requests to the health path" in {
      Get("/health") ~> Router.routes ~> check {
        responseAs[HealthResponse].time shouldBe Router.now
      }
    }

    "return a package created" in {
      Post(
        uri = "/package",
        entity = HttpEntity(
          ContentTypes.`application/json`,
          """
          |{
          |	"owner": {
          |		"id": "1234",
          |		"name": "John",
          |		"lastName": "Doe",
          |		"address": {
          |			"primarySegmentType": "St",
          |			"firstField": "42",
          |			"secondarySegmentType": "Av",
          |			"secondField": "32-09"
          |		},
          |		"telephone": "3214567"
          |	},
          |	"box": {
          |		"length": 1,
          |		"width": 5,
          |		"height": 2,
          |		"weight": 2
          |	},
          |	"status": "transit"
          |}
        """.stripMargin
        )
      ) ~> Router.routes ~> check {
        val p = responseAs[PackageDTO]
        p shouldBe Router.`package`.safeTransform[PackageDTO]
      }
    }
  }
}
