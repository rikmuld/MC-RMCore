package com.rikmuld.corerm.gui

import com.rikmuld.corerm.{RMMod, Registry}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos

object GuiSender {
  def openGui(gui: ResourceLocation, player: EntityPlayer): Unit =
    openGui(gui, player, 0, 0, 0)

  def openGui(gui: ResourceLocation, player: EntityPlayer, x: Int, y: Int, z: Int): Unit =
    player.openGui(RMMod, Registry.screenRegistry.getID(gui), player.world, x, y, z)

  def openGui(gui: ResourceLocation, player: EntityPlayer, pos: BlockPos): Unit =
    openGui(gui, player, pos.getX, pos.getY, pos.getZ)

  def openGui(gui: Int, player: EntityPlayer): Unit =
    openGui(gui, player, 0, 0, 0)

  def openGui(gui: Int, player: EntityPlayer, x: Int, y: Int, z: Int): Unit =
    player.openGui(RMMod, gui, player.world, x, y, z)

  def openGui(gui: Int, player: EntityPlayer, pos: BlockPos): Unit =
    openGui(gui, player, pos.getX, pos.getY, pos.getZ)
}