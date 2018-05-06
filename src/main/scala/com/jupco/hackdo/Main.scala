package com.jupco.hackdo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.jupco.hackdo.domain.PackagesEnvironment
import com.jupco.hackdo.domain.entities.{ Package, PackageStatus, User }
import com.jupco.hackdo.domain.repositories.PackagesRepository
import com.jupco.hackdo.domain.repositories.impl.InMemoryRepository
import com.jupco.hackdo.domain.services.impl.PackageServiceImpl
import com.jupco.hackdo.infrastructure.clients.UsersClient
import com.jupco.hackdo.infrastructure.clients.impl.MockedUsersClient
import com.jupco.hackdo.infrastructure.configuration.{ ConfigApp, DefaultConfig }
import com.jupco.hackdo.routing.SystemRoutes
import monix.eval.Task

import scala.concurrent.{ Await, ExecutionContextExecutor }
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

object Main extends App with SystemRoutes {

  implicit val system: ActorSystem                        = ActorSystem("inventory")
  implicit val materializer: ActorMaterializer            = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val environment = new PackagesEnvironment[Package, User, PackageStatus, Task, List] {

    override val packagesRepository: PackagesRepository[Package, PackageStatus, Task, List] = new InMemoryRepository

    override val usersClient: UsersClient[User, Task] = new MockedUsersClient

    override val config: ConfigApp = DefaultConfig
  }

  override val packageService = new PackageServiceImpl(environment)

  val server = Http().bindAndHandle(routes, "localhost", 8080)

  server.onComplete {
    case Success(Http.ServerBinding(localAddress)) =>
      println(s"Server online at ${localAddress.getAddress}:${localAddress.getPort}")
    case Failure(ex) =>
      println(s"There was an error while starting server, exception was ${ex.getMessage}")
      ex.getStackTrace.foreach(println)
  }

  sys.addShutdownHook {
    Await.ready(system.terminate, 10.seconds)
    ()
  }
}
