package com.rikmuld.corerm.utils

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraftforge.common.util.Constants

object NBTUtils {
  def readInventory(tag: NBTTagCompound): Map[Byte, ItemStack] = {
    val data = tag.getTagList("items", Constants.NBT.TAG_COMPOUND)
    val inventory = for (i <- 0 until data.tagCount())
      yield {
        val tag = data.getCompoundTagAt(i)
        tag.getByte("slotIndex") -> new ItemStack(tag)
      }

    inventory.toMap
  }

  def writeInventory(tag: NBTTagCompound, inventory: IInventory): NBTTagCompound = {
    val data = new NBTTagList()

    for (slot <- 0 until inventory.getSizeInventory) {
      val stack = inventory.getStackInSlot(slot)

      if(!stack.isEmpty) {
        val stackInfo = new NBTTagCompound()

        stackInfo.setByte("slotIndex", slot.toByte)
        data.appendTag(stack.writeToNBT(stackInfo))
      }
    }

    tag.setTag("items", data)
    tag
  }
}
