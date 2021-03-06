package com.jupco.hackdo.routing

import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.syntax.option._
import com.jupco.hackdo.domain.entities.{ Address, Box, Package, PackageStatus, ServiceError, User }
import com.jupco.hackdo.routing.dtos._
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.Encoder

trait Transformations extends ErrorAccumulatingCirceSupport with LazyLogging {

  def transformDirective[A, B](p: A)(f: B => Route)(implicit T: Transformation[A, B]): Route = {
    T.transform(p) match {
      case Right(b) => f(b)
      case Left(se) =>
        logger.error(s"Error found while transforming request -> $se")
        complete(BadRequest -> se)
    }
  }

  trait Transformation[A, B] {
    def transform(a: A): Either[ServiceError, B]
  }

  trait SafeTransformation[A, B] {
    def transform(a: A): B
  }

  implicit class TransformSyntax[T](val t: T) {
    def transform[U](implicit transformation: Transformation[T, U]): Either[ServiceError, U] =
      transformation.transform(t)

    def safeTransform[U](implicit safeTransformation: SafeTransformation[T, U]) = safeTransformation.transform(t)
  }

  implicit def boxDTO2Box: Transformation[BoxDTO, Box] = { boxDTO =>
    Box(boxDTO.length, boxDTO.width, boxDTO.height, boxDTO.weight)
  }

  implicit def encoderServiceError: Encoder[ServiceError] = Encoder.forProduct2("code", "message") { se =>
    (se.code, se.message)
  }

  implicit def package2PackageDTO: SafeTransformation[Package, PackageDTO] = { p =>
    PackageDTO(
      id = p.id,
      owner = p.owner.safeTransform[UserDTO],
      box = p.box.safeTransform[BoxDTO],
      status = p.status.toString
    )
  }

  implicit def user2UserDTO: SafeTransformation[User, UserDTO] = { u =>
    UserDTO(
      id = u.id,
      name = u.name,
      lastName = u.lastName,
      address = u.address.safeTransform[AddressDTO],
      telephone = u.telephone
    )
  }

  implicit def address2AddressDTO: SafeTransformation[Address, AddressDTO] = { a =>
    AddressDTO(
      primarySegmentType = a.primarySegmentType.toString,
      firstField = a.firstField,
      secondarySegmentType = a.secondarySegmentType.toString,
      secondField = a.secondField
    )
  }

  implicit def box2BoxDTO: SafeTransformation[Box, BoxDTO] = { b =>
    BoxDTO(
      length = b.length.value,
      width = b.width.value,
      height = b.height.value,
      weight = b.weight,
      volume = b.volume.value.some
    )
  }

  implicit def string2PackageStatus: Transformation[String, PackageStatus] = { s =>
    PackageStatus(s).toEither
  }
}
