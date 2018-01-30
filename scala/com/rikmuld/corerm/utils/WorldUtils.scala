package com.rikmuld.corerm.utils

import java.util.Random

import net.minecraft.entity.item.EntityItem
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object WorldUtils {
  val random = new Random()

  def dropItemInWorld(world: World, stack: ItemStack, pos: BlockPos): Unit =
    if (!world.isRemote)
      if (!stack.isEmpty) {
        val x = (random.nextFloat() * 1f) + pos.getX
        val y = (random.nextFloat() * 1f) + pos.getY
        val z = (random.nextFloat() * 1f) + pos.getZ

        val data = Option(stack.getTagCompound).map(_.copy)
        val entityItem = new EntityItem(world, x, y, z, stack.copy()) // copy probably not needed

        data.foreach(tag => entityItem.getItem.setTagCompound(tag)) // probably not needed

        entityItem.motionX = random.nextGaussian() * 0.05f
        entityItem.motionY = random.nextGaussian() * 0.05f + 0.2f
        entityItem.motionZ = random.nextGaussian() * 0.05f

        world.spawnEntity(entityItem)
        stack.setCount(0) //probably not needed
      }


  def dropItemsInWorld(world: World, stacks: Seq[ItemStack], pos: BlockPos): Unit =
    stacks.foreach(stack => dropItemInWorld(world, stack, pos))

  def dropBlockItems(world: World, pos:BlockPos): Unit =
    if (!world.isRemote)
      world.getTileEntity(pos) match {
        case inv: IInventory =>
          for (i <- 0 until inv.getSizeInventory)
            dropItemInWorld(world, inv.getStackInSlot(i), pos)
        case _ =>
      }
}
