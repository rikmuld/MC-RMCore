package com.rikmuld.corerm.tileentity

import com.rikmuld.corerm.inventory.inventory.InventorySimple
import com.rikmuld.corerm.utils.NBTUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

trait TileEntityInventory extends TileEntity with InventorySimple {
  override def isUsableByPlayer(player: EntityPlayer): Boolean =
    if (getWorld.getTileEntity(getPos) != this) false
    else player.getDistanceSq(getPos.getX + 0.5D, getPos.getY + 0.5D, getPos.getZ + 0.5D) <= 64.0D

  override def readFromNBT(tag: NBTTagCompound): Unit = {
    for((slot, stack) <- NBTUtil.readInventory(tag)) {
      setInventorySlotContents(slot, stack)
    }

    super.readFromNBT(tag)
  }

  override def writeToNBT(tag: NBTTagCompound): NBTTagCompound =
    super.writeToNBT(NBTUtil.writeInventory(tag, this))

  def openInventory(player: EntityPlayer): Unit =
    Unit

  def closeInventory(player: EntityPlayer): Unit =
    Unit
}