package com.rikmuld.corerm

import com.rikmuld.corerm.Library.TileEntities
import com.rikmuld.corerm.RMMod._
import com.rikmuld.corerm.gui.GuiHandler
import com.rikmuld.corerm.registry.{RMRegistry, Registry}
import com.rikmuld.corerm.tileentity.{TileEntityBounds, TileEntitySimple}
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry

@Mod.EventBusSubscriber@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, dependencies = MOD_DEPENDENCIES, modLanguage = MOD_LANUAGE)
object RMMod {
  final val MOD_ID = "corerm"
  final val MOD_NAME = "RikMuld's Core"
  final val MOD_VERSION = "1.3.3"
  final val MOD_LANUAGE = "scala"
  final val MOD_DEPENDENCIES = "required-after:forge@[v13.20.1.2386,)"
  final val PACKET_CHANEL = MOD_ID

  @EventHandler
  def init(event: FMLInitializationEvent) {
    GameRegistry.registerTileEntity(classOf[TileEntitySimple], TileEntities.SIMPLE)
    GameRegistry.registerTileEntity(classOf[TileEntityBounds], TileEntities.BOUNDS)

    NetworkRegistry.INSTANCE.registerGuiHandler(RMMod, new GuiHandler())

    register(event, Registry)
  }

  @SubscribeEvent
  def registerRegistries(event: RegistryEvent.NewRegistry): Unit =
    Registry.registerRegistries(event)

  def register(event: FMLInitializationEvent, registry: RMRegistry): Unit = {
    registry.registerContainers(Registry.containerRegistry)
    registry.registerPackets(Registry.packetRegistry)
    registry.registerScreens(Registry.screenRegistry, event.getSide)
    registry.registerTriggers(Registry.advancementRegistry)
  }
}