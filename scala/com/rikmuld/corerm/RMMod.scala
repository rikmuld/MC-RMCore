package com.rikmuld.corerm

import com.rikmuld.corerm.Library.ModInfo._
import com.rikmuld.corerm.Library.{Packets, TileEntities}
import com.rikmuld.corerm.advancements.triggers.TriggerOpenGUI
import com.rikmuld.corerm.network.PacketWrapper
import com.rikmuld.corerm.network.packets.{PacketBounds, PacketOpenGui, PacketTabSwitch, PacketTileData}
import com.rikmuld.corerm.registry.{RMRegistryEvent, Registry}
import com.rikmuld.corerm.tileentity.{TileEntityBounds, TileEntitySimple}
import net.minecraftforge.common.MinecraftForge
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
    MinecraftForge.EVENT_BUS.post(new RMRegistryEvent.Packets)
    MinecraftForge.EVENT_BUS.post(new RMRegistryEvent.Advancements)
    MinecraftForge.EVENT_BUS.post(new RMRegistryEvent.Containers)
    MinecraftForge.EVENT_BUS.post(new RMRegistryEvent.Screens(event.getSide))

    registerOthers()
  }

  @SubscribeEvent
  def registerRegistries(event: RegistryEvent.NewRegistry): Unit =
    Registry.registerRegistries(event)

  @SubscribeEvent
  def registerPackets(event: RMRegistryEvent.Packets): Unit =
    event.getRegistry.registerAll(
      PacketWrapper.create(classOf[PacketBounds], Packets.BOUNDS),
      PacketWrapper.create(classOf[PacketTileData], Packets.TILE_DATA),
      PacketWrapper.create(classOf[PacketTabSwitch], Packets.TAB_SWITCH),
      PacketWrapper.create(classOf[PacketOpenGui], Packets.OPEN_GUI)
    )

  @SubscribeEvent
  def registerTriggers(event: RMRegistryEvent.Advancements): Unit =
    event.getRegistry.registerAll(
      new TriggerOpenGUI.Trigger
    )

  def registerOthers(): Unit = {
    GameRegistry.registerTileEntity(classOf[TileEntitySimple], TileEntities.SIMPLE)
    GameRegistry.registerTileEntity(classOf[TileEntityBounds], TileEntities.BOUNDS)
  }
}