package com.rikmuld.corerm.inventory.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound

class InventoryPlayer(player: EntityPlayer, size: Int, stackSize: Int, id: String) extends InventoryTag {
  def loadTag(): NBTTagCompound =
    player.getEntityData.getCompoundTag(id)

  def saveTag(player: EntityPlayer): Unit =
    player.getEntityData.setTag(id, getTag)

  override def getSizeInventory: Int =
    size

  override def getInventoryStackLimit: Int =
    stackSize

  override def isUsableByPlayer(player: EntityPlayer): Boolean =
    true
}

