package com.rikmuld.corerm

import com.rikmuld.corerm.Library._
import com.rikmuld.corerm.advancements.TriggerRegistry
import com.rikmuld.corerm.gui.{ContainerWrapper, GuiHandler, ScreenWrapper}
import com.rikmuld.corerm.network.{PacketSender, PacketWrapper}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.registries.{ForgeRegistry, RegistryBuilder}

object Registry {
  var packetRegistry: ForgeRegistry[PacketWrapper] =
    _

  var screenRegistry: ForgeRegistry[ScreenWrapper] =
    _

  var containerRegistry: ForgeRegistry[ContainerWrapper] =
    _

  var advancementRegistry: TriggerRegistry =
    _

  def registerRegistries(event: RegistryEvent.NewRegistry): Unit = {
    val registryBuilderPacket = new RegistryBuilder[PacketWrapper]
    val registryBuilderScreen = new RegistryBuilder[ScreenWrapper]
    val registryBuilderContainer = new RegistryBuilder[ContainerWrapper]

    registryBuilderPacket.setName(Registries.PACKETS)
    registryBuilderPacket.setType(classOf[PacketWrapper])
    PacketSender.init()

    registryBuilderScreen.setName(Registries.SCREEN)
    registryBuilderScreen.setType(classOf[ScreenWrapper])

    registryBuilderContainer.setName(Registries.CONTAINER)
    registryBuilderContainer.setType(classOf[ContainerWrapper])

    NetworkRegistry.INSTANCE.registerGuiHandler(RMMod, new GuiHandler())

    packetRegistry =
      registryBuilderPacket.create().asInstanceOf[ForgeRegistry[PacketWrapper]]

    screenRegistry =
      registryBuilderScreen.create().asInstanceOf[ForgeRegistry[ScreenWrapper]]

    containerRegistry =
      registryBuilderContainer.create().asInstanceOf[ForgeRegistry[ContainerWrapper]]

    advancementRegistry =
      new TriggerRegistry

    MinecraftForge.EVENT_BUS.post(new RegistryEvent.Register[PacketWrapper](Registries.PACKETS, packetRegistry))
    MinecraftForge.EVENT_BUS.post(new RegistryEvent.Register[ScreenWrapper](Registries.SCREEN, screenRegistry))
    MinecraftForge.EVENT_BUS.post(new RegistryEvent.Register[ContainerWrapper](Registries.CONTAINER, containerRegistry))
  }
}
