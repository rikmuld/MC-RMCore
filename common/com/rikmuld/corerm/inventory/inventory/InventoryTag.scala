package com.rikmuld.corerm.inventory.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraftforge.common.util.Constants

trait InventoryTag extends InventorySimple {
  private val tag: NBTTagCompound =
    loadTag()

  def closeInventory(player:EntityPlayer) =
    saveTag(player)

  def openInventory(player:EntityPlayer) = {
    readItems(tag)
    writeItems(tag)
  }

  def markDirty(): Unit =
    writeItems(tag)

  def getTag: NBTTagCompound =
    tag

  def loadTag(): NBTTagCompound

  def saveTag(player: EntityPlayer): Unit

  def readItems(tag: NBTTagCompound) {
    val inventory = tag.getTagList("items", Constants.NBT.TAG_COMPOUND)

    for (i <- 0 until inventory.tagCount()) {
      val stackInfo = inventory.getCompoundTagAt(i)
      val index = stackInfo.getByte("slotIndex")

      setInventorySlotContents(index, new ItemStack(stackInfo))
    }
  }

  def writeItems(tag: NBTTagCompound) {
    val inventory = new NBTTagList()

    for (slot <- 0 until getSizeInventory) {
      val stack = getStackInSlot(slot)
      if(!stack.isEmpty) {
        val stackInfo = new NBTTagCompound()

        stackInfo.setByte("slotIndex", slot.toByte)
        inventory.appendTag(stack.writeToNBT(stackInfo))
      }
    }

    tag.setTag("items", inventory)
  }
}