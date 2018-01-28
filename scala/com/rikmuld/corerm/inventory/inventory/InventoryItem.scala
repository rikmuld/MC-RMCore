package com.rikmuld.corerm.inventory.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class InventoryItem(stack: ItemStack, size: Int, stackSize: Int) extends InventoryTag {
  def loadTag(): NBTTagCompound =
    if (stack.hasTagCompound) stack.getTagCompound
    else new NBTTagCompound()

  def saveTag(player: EntityPlayer): Unit =
    stack.setTagCompound(getTag)

  override def getInventoryStackLimit: Int =
    stackSize

  override def getSizeInventory: Int =
    size

  override def isUsableByPlayer(player: EntityPlayer): Boolean =
    player.inventory.getCurrentItem.isItemEqual(stack)

  def getStack: ItemStack =
    stack

  override def getName: String =
    "inventory_" + stack.getItem.getRegistryName.getResourcePath
}