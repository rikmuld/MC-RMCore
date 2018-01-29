package com.rikmuld.corerm.gui

import com.rikmuld.corerm.Registry
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.registries.IForgeRegistryEntry

object ScreenWrapper {
  def create[A <: GuiScreen](side: Side, screen: => Class[A]): ScreenWrapper =
    new ScreenWrapper(
      if(side == Side.SERVER) None
      else Some(screen.asInstanceOf[Class[GuiScreen]])
    )

  def create[A <: GuiScreen](side: Side, screen: => Class[A], name: String): ScreenWrapper =
    create(side, screen).setRegistryName(name)

  def create[A <: GuiScreen](side: Side, screen: => Class[A], name: ResourceLocation): ScreenWrapper =
    create(side, screen).setRegistryName(name)
}

class ScreenWrapper(screen: Option[Class[GuiScreen]]) extends IForgeRegistryEntry.Impl[ScreenWrapper] {
  val typ: Int =
    screen.fold(0)(_.getConstructors.apply(0).getParameterTypes.length)

  var createFunction: (EntityPlayer, World, BlockPos) => GuiScreen =
    _

  def getScreen: Option[Class[GuiScreen]] =
    screen

  def getID: Int =
    Registry.screenRegistry.getID(this)

  //dangerous!
  def create(player: EntityPlayer, world: World, pos: BlockPos): GuiScreen =
    Option(createFunction).fold(
      typ match {
        case 0 =>
          screen.get.newInstance
        case 1 =>
          screen.get.getConstructor(classOf[EntityPlayer]).newInstance(player)
        case 2 =>
          screen.get.getConstructor(classOf[EntityPlayer], classOf[IInventory])
            .newInstance(player, world.getTileEntity(pos))
      }
    )(_.apply(player, world, pos))

  def alternative(f: (EntityPlayer, World, BlockPos) => GuiScreen): Unit =
    createFunction = f
}