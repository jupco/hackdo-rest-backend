package com.jupco.hackdo.infrastructure.configuration

import com.jupco.hackdo.TestSpec

class DefaultConfigTests extends TestSpec {

  "DefaultConfig" should {
    "load a configuration from the application.conf file" in {
      DefaultConfig.inventoryConfiguration.maxVolumePerBox shouldBe 40
    }
  }
}
