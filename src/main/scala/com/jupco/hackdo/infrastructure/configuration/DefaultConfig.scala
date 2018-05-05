package com.jupco.hackdo.infrastructure.configuration

import com.typesafe.config.{ Config, ConfigFactory }
import pureconfig.loadConfig

object DefaultConfig extends ConfigApp {

  val config: Config = ConfigFactory.load()

  override def inventoryConfiguration: InventoryConfiguration =
    loadConfig[InventoryConfiguration](config, "inventory.config-params")
      .fold(_ => throw new Exception("COnfig is malformed"), identity)
}
