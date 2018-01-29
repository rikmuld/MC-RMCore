package com.rikmuld.corerm.gui

import com.rikmuld.corerm.Registry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{Container, IInventory}
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry

object ContainerWrapper {
  def create[A <: Container](container: Class[A], name: String): ContainerWrapper =
    new ContainerWrapper(container.asInstanceOf[Class[Container]]).setRegistryName(name)

  def create[A <: Container](container: Class[A], name: ResourceLocation): ContainerWrapper =
    new ContainerWrapper(container.asInstanceOf[Class[Container]]).setRegistryName(name)
}

class ContainerWrapper(container: Class[Container]) extends IForgeRegistryEntry.Impl[ContainerWrapper] {
  val typ: Int =
    container.getConstructors.apply(0).getParameterTypes.length

  var createFunction: (EntityPlayer, World, BlockPos) => Container =
    _

  def getContainer: Class[Container] =
    container

  def getID: Int =
    Registry.containerRegistry.getID(this)

  def create(player: EntityPlayer, world: World, pos: BlockPos): Container =
    Option(createFunction).fold(
      typ match {
        case 0 =>
          container.newInstance
        case 1 =>
          container.getConstructor(classOf[EntityPlayer]).newInstance(player)
        case 2 =>
          println(player, world.getTileEntity(pos))
          container.getConstructor(classOf[EntityPlayer], classOf[IInventory])
            .newInstance(player, world.getTileEntity(pos))
      }
    )(_.apply(player, world, pos))

  def alternative(f: (EntityPlayer, World, BlockPos) => Container): Unit =
    createFunction = f
}