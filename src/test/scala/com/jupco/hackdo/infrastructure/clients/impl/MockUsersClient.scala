package com.jupco.hackdo.infrastructure.clients.impl

import com.jupco.hackdo.domain.entities.{ Address, Avenue, Street, User }
import com.jupco.hackdo.infrastructure.clients.UsersClient
import monix.eval.Task

trait MockUsersClient extends UsersClient[User, Task] {

  val users = List(
    User(id = "1", name = "John", lastName = "Doe", address = Address(Street, "1st", Avenue, "23 South"), "123456789"),
    User(id = "2", name = "Mark", lastName = "Bob", address = Address(Street, "2st", Avenue, "23 South"), "123456789"),
    User(
      id = "3",
      name = "Samuel",
      lastName = "Cooper",
      address = Address(Street, "3st", Avenue, "23 South"),
      "123456789"
    ),
    User(
      id = "4",
      name = "Vivian",
      lastName = "Dilan",
      address = Address(Street, "4st", Avenue, "23 South"),
      "123456789"
    ),
    User(id = "5", name = "Will", lastName = "Foo", address = Address(Street, "5st", Avenue, "23 South"), "123456789"),
    User(id = "6", name = "Sarah", lastName = "Bar", address = Address(Street, "6st", Avenue, "23 South"), "123456789"),
    User(
      id = "7",
      name = "Ash",
      lastName = "Ketchup",
      address = Address(Street, "7st", Avenue, "23 South"),
      "123456789"
    ),
    User(
      id = "8",
      name = "Triss",
      lastName = "Merigold",
      address = Address(Street, "8st", Avenue, "23 South"),
      "123456789"
    ),
    User(
      id = "9",
      name = "Gerard",
      lastName = "Bond",
      address = Address(Street, "9st", Avenue, "23 South"),
      "123456789"
    ),
    User(
      id = "10",
      name = "Johnny",
      lastName = "Bonaparte",
      address = Address(Street, "10st", Avenue, "23 South"),
      "123456789"
    )
  )

  override def getUserById(id: String): Task[Option[User]] = Task.now {
    users.find(_.id == id)
  }

}

object MockUsersClient extends MockUsersClient
