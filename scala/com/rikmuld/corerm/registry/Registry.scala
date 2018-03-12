package com.rikmuld.corerm.registry

import com.rikmuld.corerm.Library._
import com.rikmuld.corerm.advancements.TriggerRegistry
import com.rikmuld.corerm.advancements.triggers.TriggerOpenGUI
import com.rikmuld.corerm.gui.{ContainerWrapper, ScreenWrapper}
import com.rikmuld.corerm.network.packets.{PacketBounds, PacketOpenGui, PacketTabSwitch, PacketTileData}
import com.rikmuld.corerm.network.{PacketSender, PacketWrapper}
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.registries.{ForgeRegistry, RegistryBuilder}

object Registry extends RMRegistry {
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

    packetRegistry =
      registryBuilderPacket.create().asInstanceOf[ForgeRegistry[PacketWrapper]]

    screenRegistry =
      registryBuilderScreen.create().asInstanceOf[ForgeRegistry[ScreenWrapper]]

    containerRegistry =
      registryBuilderContainer.create().asInstanceOf[ForgeRegistry[ContainerWrapper]]

    advancementRegistry =
      new TriggerRegistry
  }

  override def registerPackets(registry: ForgeRegistry[PacketWrapper]): Unit =
    registry.registerAll(
      PacketWrapper.create(classOf[PacketBounds], Packets.BOUNDS),
      PacketWrapper.create(classOf[PacketTileData], Packets.TILE_DATA),
      PacketWrapper.create(classOf[PacketTabSwitch], Packets.TAB_SWITCH),
      PacketWrapper.create(classOf[PacketOpenGui], Packets.OPEN_GUI)
    )

  override def registerTriggers(registry: TriggerRegistry): Unit =
    registry.registerAll(
      new TriggerOpenGUI.Trigger
    )
}
