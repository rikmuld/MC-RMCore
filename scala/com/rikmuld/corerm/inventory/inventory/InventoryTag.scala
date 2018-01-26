package com.rikmuld.corerm.inventory.inventory

import com.rikmuld.corerm.utils.NBTUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound

trait InventoryTag extends InventorySimple {
  private val tag: NBTTagCompound =
    loadTag()

  def closeInventory(player:EntityPlayer): Unit =
    saveTag(player)

  def openInventory(player:EntityPlayer): Unit = {
    readItems(tag)
    writeItems(tag)
  }

  def markDirty(): Unit =
    writeItems(tag)

  def getTag: NBTTagCompound =
    tag

  def loadTag(): NBTTagCompound

  def saveTag(player: EntityPlayer): Unit

  def readItems(tag: NBTTagCompound): Unit =
    for((slot, stack) <- NBTUtil.readInventory(tag)){
      setInventorySlotContents(slot, stack)
    }

  def writeItems(tag: NBTTagCompound): NBTTagCompound =
    NBTUtil.writeInventory(tag, this)
}