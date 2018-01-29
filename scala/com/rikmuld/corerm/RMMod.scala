package com.rikmuld.corerm

import com.rikmuld.corerm.Library.ModInfo._
import com.rikmuld.corerm.Library.Packets
import com.rikmuld.corerm.advancements.triggers.TriggerOpenGUI
import com.rikmuld.corerm.network.PacketWrapper
import com.rikmuld.corerm.network.packets.{PacketBounds, PacketTabSwitch, PacketTileData}
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
    GameRegistry.registerTileEntity(classOf[TileEntitySimple], MOD_ID + "_coreTile")
    GameRegistry.registerTileEntity(classOf[TileEntityBounds], MOD_ID + "_boundsTile")

    Registry.advancementRegistry.register(new TriggerOpenGUI.Trigger)
  }

  @SubscribeEvent
  def registerRegistries(event: RegistryEvent.NewRegistry): Unit =
    Registry.registerRegistries(event)

  @SubscribeEvent
  def registerPackets(event: RegistryEvent.Register[PacketWrapper]): Unit =
    Registry.packetRegistry.registerAll(
      PacketWrapper.create(classOf[PacketBounds], Packets.BOUNDS),
      PacketWrapper.create(classOf[PacketTileData], Packets.TILE_DATA),
      PacketWrapper.create(classOf[PacketTabSwitch], Packets.TAB_SWITCH)
    )
}