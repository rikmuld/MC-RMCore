package com.rikmuld.corerm.inventory

import com.rikmuld.corerm.utils.NBTUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound

trait InventoryTag extends InventorySimple {
  private val tag: NBTTagCompound =
    loadTag()

  private var opened: Boolean =
    false

  override def isOpen: Boolean =
    opened

  def closeInventory(player:EntityPlayer): Unit =
    saveTag(player)

  def openInventory(player:EntityPlayer): Unit = {
    readItems(tag)
    writeItems(tag)

    opened = true
  }

  def markDirty(): Unit =
    writeItems(tag)

  def getTag: NBTTagCompound =
    tag

  def loadTag(): NBTTagCompound

  def saveTag(player: EntityPlayer): Unit

  def readItems(tag: NBTTagCompound): Unit =
    for((slot, stack) <- NBTUtils.readInventory(tag)){
      setInventorySlotContents(slot, stack)
    }

  def writeItems(tag: NBTTagCompound): NBTTagCompound =
    NBTUtils.writeInventory(tag, this)
}