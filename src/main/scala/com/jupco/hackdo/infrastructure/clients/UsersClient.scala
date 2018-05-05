package com.jupco.hackdo.infrastructure.clients

trait UsersClient[User, T[_]] {

  def getUserById(id: String): T[Option[User]]

}
