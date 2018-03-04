package com.rikmuld.corerm

import com.rikmuld.corerm.Library.ModInfo._
import com.rikmuld.corerm.Library.TileEntities
import com.rikmuld.corerm.registry.{RMRegistry, Registry}
import com.rikmuld.corerm.tileentity.{TileEntityBounds, TileEntitySimple}
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry

@Mod.EventBusSubscriber@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, dependencies = MOD_DEPENDENCIES, modLanguage = MOD_LANUAGE)
object RMMod {

  @EventHandler
  def init(event: FMLInitializationEvent) {
    GameRegistry.registerTileEntity(classOf[TileEntitySimple], TileEntities.SIMPLE)
    GameRegistry.registerTileEntity(classOf[TileEntityBounds], TileEntities.BOUNDS)

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