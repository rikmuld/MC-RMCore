package com.rikmuld.corerm.gui

import com.rikmuld.corerm.RMMod
import com.rikmuld.corerm.network.PacketSender
import com.rikmuld.corerm.network.packets.PacketOpenGui
import com.rikmuld.corerm.registry.Registry
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos

object GuiHelper {
  def openGui(gui: ResourceLocation, player: EntityPlayer): Boolean =
    openGui(gui, player, 0, 0, 0)

  def openGui(gui: ResourceLocation, player: EntityPlayer, x: Int, y: Int, z: Int): Boolean =
    hasContainer(gui) match {
      case false if player.world.isRemote =>
        player.openGui(RMMod, Registry.screenRegistry.getID(gui), player.world, x, y, z)
        true
      case true if !player.world.isRemote =>
        player.openGui(RMMod, Registry.screenRegistry.getID(gui), player.world, x, y, z)
        true
      case _ =>
        false
    }

  def openGui(gui: ResourceLocation, player: EntityPlayer, pos: BlockPos): Boolean =
    openGui(gui, player, pos.getX, pos.getY, pos.getZ)

  def openGui(gui: Int, player: EntityPlayer): Unit =
    openGui(gui, player, 0, 0, 0)

  def openGui(gui: Int, player: EntityPlayer, x: Int, y: Int, z: Int): Unit =
    player.openGui(RMMod, gui, player.world, x, y, z)

  def openGui(gui: Int, player: EntityPlayer, pos: BlockPos): Unit =
    openGui(gui, player, pos.getX, pos.getY, pos.getZ)

  def hasContainer(gui: ResourceLocation): Boolean =
    Option(Registry.containerRegistry.getValue(gui)).isDefined

  def forceOpenGui(gui: ResourceLocation, player: EntityPlayer, pos: BlockPos): Unit =
    forceOpenGui(gui, player, pos.getX, pos.getY, pos.getZ)

  def forceOpenGui(gui: ResourceLocation, player: EntityPlayer): Unit =
    forceOpenGui(gui, player, 0, 0, 0)

  def forceOpenGui(gui: ResourceLocation, player: EntityPlayer, x: Int, y: Int, z: Int): Unit =
    if(!openGui(gui, player, x, y, z))
      if(!player.world.isRemote)
        PacketSender.sendToPlayer(new PacketOpenGui(gui, x, y, z), player.asInstanceOf[EntityPlayerMP])
      else
        PacketSender.sendToServer(new PacketOpenGui(gui, x, y, z))

}