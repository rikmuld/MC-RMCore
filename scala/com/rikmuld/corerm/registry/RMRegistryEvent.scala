package com.rikmuld.corerm.registry

import com.rikmuld.corerm.advancements.TriggerRegistry
import com.rikmuld.corerm.gui.{ContainerWrapper, ScreenWrapper}
import com.rikmuld.corerm.network.PacketWrapper
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.registries.IForgeRegistry

object RMRegistryEvent {
  class Packets extends Event {
    def getRegistry: IForgeRegistry[PacketWrapper] =
      Registry.packetRegistry
  }

  class Containers extends Event {
    def getRegistry: IForgeRegistry[ContainerWrapper] =
      Registry.containerRegistry
  }

  class Screens(side: Side) extends Event {
    def getRegistry: IForgeRegistry[ScreenWrapper] =
      Registry.screenRegistry

    def getSide: Side =
      side
  }

  class Advancements extends Event {
    def getRegistry: TriggerRegistry =
      Registry.advancementRegistry
  }
}