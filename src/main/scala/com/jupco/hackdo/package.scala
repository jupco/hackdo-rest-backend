package com.jupco

import cats.data.EitherT
import com.jupco.hackdo.domain.entities.ServiceError
import monix.eval.Task

package object hackdo {

  type ServiceResponse[A] = EitherT[Task, ServiceError, A]
}
