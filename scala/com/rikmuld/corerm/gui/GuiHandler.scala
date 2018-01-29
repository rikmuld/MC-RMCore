package com.rikmuld.corerm.gui

import com.rikmuld.corerm.Registry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

class GuiHandler extends IGuiHandler {
  def getServerGuiElement(gui: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Object = {
    val name = Registry.screenRegistry.getValue(gui).getRegistryName

    Option(Registry.containerRegistry.getValue(name)).map(container =>
      container.create(player, world, new BlockPos(x, y, z))
    ).orNull
  }

  @SideOnly(Side.CLIENT)
  def getClientGuiElement(gui: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Object =
    Option(Registry.screenRegistry.getValue(gui)).map(screen =>
      screen.create(player, world, new BlockPos(x, y, z))
    ).orNull
}