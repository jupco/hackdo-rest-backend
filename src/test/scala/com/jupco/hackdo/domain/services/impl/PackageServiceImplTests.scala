package com.jupco.hackdo.domain.services.impl

import com.jupco.hackdo.TestSpec
import com.jupco.hackdo.domain.PackagesEnvironment
import com.jupco.hackdo.domain.entities.{ Address, Avenue, Box, Dimension, Package, PackageStatus, Received, Review, Street, Transit, User }
import com.jupco.hackdo.domain.repositories.PackagesRepository
import com.jupco.hackdo.infrastructure.clients.UsersClient
import com.jupco.hackdo.infrastructure.clients.impl.MockUsersClient
import com.jupco.hackdo.infrastructure.configuration.{ ConfigApp, InventoryConfiguration }
import monix.eval.Task
import monix.execution.Scheduler

class PackageServiceImplTests extends TestSpec {

  "PackageService" should {

    import PackageServiceImplTests._
    implicit val sc: Scheduler = Scheduler.io()

    "not create a Package" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.createPackage(package1)
      r.fold(
          _.message shouldBe "the user u1 doesn't exist",
          _ => fail
        )
        .runAsync
    }

    "create a Package" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.createPackage(package3)
      r.fold(
          _ => fail,
          _ shouldBe package3
        )
        .runAsync
    }

    "getPackagesByUserId for an user" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.getPackagesByUserId(package3.owner.id)
      r.fold(
          _ => fail,
          _ shouldBe List(package3, package4)
        )
        .runAsync
    }

    "not getPackagesByUserId with an invalid user" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.getPackagesByUserId(package1.owner.id)
      r.fold(
          _.message shouldBe "the user u1 doesn't exist",
          _ => fail
        )
        .runAsync
    }

    "getPackageByPackageId" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.getPackageByPackageId("p3")
      r.fold(
          _ => fail,
          _ shouldBe package3
        )
        .runAsync
    }

    "not getPackageByPackageId with an invalid id" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.getPackageByPackageId("0000")
      r.fold(
          _.message shouldBe "the package with id 0000 wasn't found in the system",
          _ => fail
        )
        .runAsync
    }

    "getPackagesByStatus" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.getPackagesByStatus(Transit)
      r.fold(
          _ => fail,
          _ shouldBe List(package3, package4)
        )
        .runAsync
    }

    "not getPackagesByStatus" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.getPackagesByStatus(Received)
      r.fold(
          _ => fail,
          _ shouldBe Nil
        )
        .runAsync
    }

    "updateBox" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.updateBox("p3", Box(new Dimension(1), new Dimension(1), new Dimension(1), 5))
      r.fold(
          _ => fail,
          _.box.volume.value shouldBe 1
        )
        .runAsync
    }

    "not updateBox in an non-existent package " in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.updateBox("000", bigBox.copy(length = new Dimension(1)))
      r.fold(
          _.message shouldBe "the package with id 000 wasn't found in the system",
          _ => fail
        )
        .runAsync
    }

    "not updateBox beacuse its size" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.updateBox(package3.id, bigBox)
      r.fold(
          _.message shouldBe "This Box exceeds the maximum volume which this storage is able to store",
          _ => fail
        )
        .runAsync
    }

    "updateStatus" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.updateStatus("p3", Review)
      r.fold(
          _ => fail,
          _.status shouldBe Review
        )
        .runAsync
    }

    "not updateStatus in a non-existent package" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.updateStatus("000", Review)
      r.fold(
          _.message shouldBe "the package with id 000 wasn't found in the system",
          _ => fail
        )
        .runAsync
    }

    "deletePackage" in {
      val p = new PackageServiceImpl(mockedEnv)
      val r = p.deletePackage(package3)
      r.fold(
          _ => fail,
          _ shouldBe package3
        )
        .runAsync
    }

  }

}

object PackageServiceImplTests {

  lazy val mockedEnv = new PackagesEnvironment[Package, User, PackageStatus, Task, List] {

    override def packagesRepository: PackagesRepository[Package, PackageStatus, Task, List] = mockedRepo

    override def usersClient: UsersClient[User, Task] = userClient

    override def config: ConfigApp = new ConfigApp {
      override def inventoryConfiguration: InventoryConfiguration = InventoryConfiguration(maxVolumePerBox = 40)
    }
  }

  lazy val mockedRepo = new PackagesRepository[Package, PackageStatus, Task, List] {

    override def storeOrUpdate(p: Package): Task[Package] = Task.now(p)

    override def getByPackageId(id: String): Task[Option[Package]] = Task.now {
      if (id == "p3") Some(package3)
      else None
    }

    override def getByUserId(userId: String): Task[List[Package]] = Task.now {
      if (userId == userClient.users.head.id) List(package3, package4)
      else Nil
    }

    override def getByStatus(status: PackageStatus): Task[List[Package]] = Task.now {
      if (status == Transit) List(package3, package4)
      else Nil
    }

    override def delete(p: Package): Task[Package] = Task.now(p)
  }

  lazy val userClient = MockUsersClient

  lazy val package1 = Package(
    id = "p1",
    owner = User("u1", "name1", "last1", add, "12345"),
    box = Box(1, 2, 3, 4).getOrElse(throw new Exception),
    status = Transit
  ) // User not found

  lazy val bigBox = Box(10, 2, 3, 4).getOrElse(throw new Exception)

  lazy val package3 = Package(
    id = "p3",
    owner = userClient.users.head,
    box = Box(2, 2, 3, 4).getOrElse(throw new Exception),
    status = Transit
  )

  lazy val package4 = Package(
    id = "p2",
    owner = userClient.users.head,
    box = Box(2, 3, 3, 4).getOrElse(throw new Exception),
    status = Transit
  )

  lazy val add = Address(Street, "1", Avenue, "1231")

}
