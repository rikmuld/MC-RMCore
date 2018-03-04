package com.rikmuld.corerm.registry

import com.rikmuld.corerm.advancements.TriggerRegistry
import com.rikmuld.corerm.gui.{ContainerWrapper, ScreenWrapper}
import com.rikmuld.corerm.network.PacketWrapper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.registries.ForgeRegistry

trait RMRegistry {
  def registerScreens(registry: ForgeRegistry[ScreenWrapper], side: Side): Unit =
    Unit

  def registerContainers(registry: ForgeRegistry[ContainerWrapper]): Unit =
    Unit

  def registerPackets(registry: ForgeRegistry[PacketWrapper]): Unit =
    Unit

  def registerTriggers(registry: TriggerRegistry): Unit =
    Unit
}