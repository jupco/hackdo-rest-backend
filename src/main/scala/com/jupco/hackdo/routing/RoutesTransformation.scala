package com.jupco.hackdo.routing

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes.{ BadRequest, InternalServerError, OK }
import akka.http.scaladsl.server.Directives.{ complete, onComplete }
import akka.http.scaladsl.server.Route
import com.jupco.hackdo.ServiceResponse
import monix.execution.Scheduler

import scala.util.{ Failure, Success }

trait RoutesTransformation { self: Transformations =>

  implicit def sc: Scheduler

  def onCompleteEitherT[T, S: ToEntityMarshaller](
      e: ServiceResponse[T]
  )(success: T => S, statusCodeFailure: StatusCode = BadRequest, statusCodeSuccess: StatusCode = OK): Route = {
    onComplete(
      e.fold(
          se => complete(statusCodeFailure     -> se),
          result => complete(statusCodeSuccess -> success(result))
        )
        .runAsync
    ) {
      case Success(route) => route
      case Failure(exception) =>
        logger.error(exception.getMessage)
        exception.getStackTrace.foreach(e => logger.debug(e.toString))
        complete(InternalServerError)
    }
  }
}
