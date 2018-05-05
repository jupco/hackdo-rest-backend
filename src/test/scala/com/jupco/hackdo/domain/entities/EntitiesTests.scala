package com.jupco.hackdo.domain.entities

import cats.data.Validated.{ Invalid, Valid }
import com.jupco.hackdo.TestSpec

class EntitiesTests extends TestSpec {

  "Address Value Object" should {

    "return a valid Address" in {
      val address = Address("St", "1", "Avenue", "14")
      address match {
        case Right(add) =>
          add.primarySegmentType shouldBe Street
          add.firstField shouldBe "1"
          add.secondarySegmentType shouldBe Avenue
          add.secondField shouldBe "14"
        case Left(_) => fail()
      }
    }

    "return an invalid Address" in {
      val address = Address("Cll", "1", "Avenue", "14")
      address match {
        case Right(_) => fail()
        case Left(x)  => x.map(_.message) shouldBe List("Cll is not a valid value for the address segment type")
      }
    }
  }

  "Box value object" should {

    "return a valid box" in {
      val box = Box(1, 2, 3, 4)
      box match {
        case Right(b) =>
          b.length.value shouldBe 1
          b.width.value shouldBe 2
          b.height.value shouldBe 3
          b.weight shouldBe 4
          b.volume shouldBe Volume(6)
        case Left(_) => fail()
      }
    }

    "return an invalid box" in {
      val box = Box(-1, 2, 3, 4)
      box match {
        case Right(_) => fail()
        case Left(x)  => x.message shouldBe "-1.0 is not a valid value for a box dimension"
      }
    }
  }

  "Volume value object" should {
    "compare successfully" in {
      val v1   = Volume(1)
      val v2_1 = Volume(2)
      val v2_2 = Volume(2)
      val v3   = Volume(3)
      v1 < v2_1 shouldBe true
      v1 <= v2_1 shouldBe true
      v2_1 <= v2_2 shouldBe true
      v2_1 == v2_2 shouldBe true
      v2_1 >= v2_2 shouldBe true
      v3 > v2_1 shouldBe true
      v3 >= v2_1 shouldBe true
    }
  }

  "Package Status value object" should {
    "return the correct status (Transit)" in {
      val status = PackageStatus("Transit")
      status match {
        case Valid(s)   => s shouldBe Transit
        case Invalid(_) => fail()
      }
    }

    "return the correct status (Delivered)" in {
      val status = PackageStatus("Delivered")
      status match {
        case Valid(s)   => s shouldBe Delivered
        case Invalid(_) => fail()
      }
    }

    "return the correct status (Review)" in {
      val status = PackageStatus("Review")
      status match {
        case Valid(s)   => s shouldBe Review
        case Invalid(_) => fail()
      }
    }

    "return the correct status (Received)" in {
      val status = PackageStatus("Received")
      status match {
        case Valid(s)   => s shouldBe Received
        case Invalid(_) => fail()
      }
    }

    "return an invalid status" in {
      val status = PackageStatus("status")
      status match {
        case Valid(_)   => fail
        case Invalid(s) => s.message shouldBe "status is not a valid package status"
      }
    }
  }

}
