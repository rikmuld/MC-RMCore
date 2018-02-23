package com.rikmuld.corerm.tileentity

import com.rikmuld.corerm.inventory.InventorySimple
import com.rikmuld.corerm.utils.NBTUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.{ITextComponent, TextComponentString}

abstract class TileEntityInventory extends TileEntitySimple with InventorySimple {
  private var open: Boolean =
    false

  override def isUsableByPlayer(player: EntityPlayer): Boolean =
    if (getWorld.getTileEntity(getPos) != this) false
    else player.getDistanceSq(getPos.getX + 0.5D, getPos.getY + 0.5D, getPos.getZ + 0.5D) <= 64.0D

  override def readFromNBT(tag: NBTTagCompound): Unit = {
    for((slot, stack) <- NBTUtils.readInventory(tag)) {
      setInventorySlotContents(slot, stack)
    }

    super.readFromNBT(tag)

    open = true
  }

  override def writeToNBT(tag: NBTTagCompound): NBTTagCompound =
    super.writeToNBT(NBTUtils.writeInventory(tag, this))

  override def isOpen: Boolean =
    open

  override def onLoad(): Unit = {
    println(world.isRemote)
    open = true
  }

  def openInventory(player: EntityPlayer): Unit =
    Unit

  def closeInventory(player: EntityPlayer): Unit =
    Unit

  override def getDisplayName: ITextComponent =
    new TextComponentString(getName)
}