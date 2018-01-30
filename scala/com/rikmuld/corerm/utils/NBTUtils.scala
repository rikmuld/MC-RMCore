package com.rikmuld.corerm.utils

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraftforge.common.util.Constants

object NBTUtils {
  def readInventory(tag: NBTTagCompound): Map[Byte, ItemStack] = {
    val data = tag.getTagList("items", Constants.NBT.TAG_COMPOUND)
    for (i <- 0 until data.tagCount())
      yield {
        val tag = data.getCompoundTagAt(i)
        tag.getByte("slotIndex") -> new ItemStack(tag)
      }
  }.toMap

  def writeInventory(tag: NBTTagCompound, inventory: IInventory): NBTTagCompound =
    writeInventory(tag, inventoryToItems(inventory).toMap)

  def writeInventory(tag: NBTTagCompound, inventory: Map[Byte, ItemStack]): NBTTagCompound = {
    val data = new NBTTagList()

    inventory.foreach {
      case(slot, stack) if !stack.isEmpty =>
        val stackInfo = new NBTTagCompound()

        stackInfo.setByte("slotIndex", slot.toByte)
        data.appendTag(stack.writeToNBT(stackInfo))
      case _ =>
    }

    tag.setTag("items", data)
    tag
  }

  private def inventoryToItems(inventory: IInventory): Seq[(Byte, ItemStack)] =
    for (i <- 0 until inventory.getSizeInventory if !inventory.getStackInSlot(i).isEmpty)
      yield i.toByte -> inventory.getStackInSlot(i)
}
