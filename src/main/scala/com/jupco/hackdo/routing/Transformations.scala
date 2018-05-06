package com.jupco.hackdo.routing

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.jupco.hackdo.domain.entities.{ Address, Box, Package, PackageStatus, ServiceError, User }
import com.jupco.hackdo.routing.dtos.{ AddressDTO, BoxDTO, PackageDTO, UserDTO }
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.Encoder

trait Transformations extends ErrorAccumulatingCirceSupport {

  def transformDirective[A, B](p: A)(f: B => Route)(implicit T: Transformation[A, B]): Route = {
    T.transform(p) match {
      case Right(b) => f(b)
      case Left(se) => complete(BadRequest -> se)
    }
  }

  trait Transformation[A, B] {
    def transform(a: A): Either[ServiceError, B]
  }

  implicit def packageDTO2Package: Transformation[PackageDTO, Package] = { packageDTO =>
    for {
      own <- userDTO2User.transform(packageDTO.owner)
      box <- boxDTO2Box.transform(packageDTO.box)
      sta <- PackageStatus(packageDTO.status).toEither
      id = UUID.randomUUID().toString
    } yield Package(id, own, box, sta)
  }

  implicit def userDTO2User: Transformation[UserDTO, User] = { userDTO =>
    for {
      add <- addressDTO2Address.transform(userDTO.address)
    } yield User(userDTO.id, userDTO.name, userDTO.lastName, add, userDTO.telephone)
  }

  implicit def addressDTO2Address: Transformation[AddressDTO, Address] = { addressDTO =>
    Address(
      addressDTO.primarySegmentType,
      addressDTO.firstField,
      addressDTO.secondarySegmentType,
      addressDTO.secondField
    )
  }

  implicit def boxDTO2Box: Transformation[BoxDTO, Box] = { boxDTO =>
    Box(boxDTO.length, boxDTO.width, boxDTO.height, boxDTO.weight)
  }

  implicit def encoderServiceError: Encoder[ServiceError] = Encoder.forProduct2("code", "message") { se =>
    (se.code, se.message)
  }
}
