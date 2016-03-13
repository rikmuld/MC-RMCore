package com.rikmuld.corerm.objs

import com.rikmuld.corerm.network.PacketSender
import net.minecraft.util.BlockPos
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import net.minecraft.network.Packet
import com.rikmuld.corerm.network.TileData
import net.minecraft.init.Blocks
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import net.minecraft.util.IChatComponent
import net.minecraft.util.ChatComponentText
import net.minecraft.server.gui.IUpdatePlayerListBox

class RMTile extends TileEntity {
  override def readFromNBT(tag: NBTTagCompound) = super.readFromNBT(tag)
  override def writeToNBT(tag: NBTTagCompound) = super.writeToNBT(tag)
  override def getDescriptionPacket(): Packet = {
    val compound = new NBTTagCompound();
    writeToNBT(compound);
    new S35PacketUpdateTileEntity(new BlockPos(this.pos.getX, this.pos.getY, this.pos.getZ), 1, compound);
  }
  def bd = (worldObj, pos)
  override def onDataPacket(net: NetworkManager, packet: S35PacketUpdateTileEntity) = readFromNBT(packet.getNbtCompound());
  def setTileData(id: Int, data: Array[Int]) {}
  def sendTileData(id: Int, toClient: Boolean, data: Int*) {
    if (!toClient && worldObj.isRemote) PacketSender.toServer(new TileData(id, pos.getX, pos.getY, pos.getZ, data));
    else if (toClient && !worldObj.isRemote) PacketSender.toClient(new TileData(id, pos.getX, pos.getY, pos.getZ, data));
  }
}

abstract trait WithTileInventory extends TileEntity with IInventory {
  var inventoryContents: Array[ItemStack] = new Array[ItemStack](getSizeInventory)

  override def decrStackSize(slot: Int, amount: Int): ItemStack = {
    if (inventoryContents(slot) != null) {
      var itemstack: ItemStack = null
      if (inventoryContents(slot).stackSize <= amount) {
        itemstack = inventoryContents(slot)
        inventoryContents(slot) = null
        onInventoryChanged(slot)
        itemstack
      } else {
        itemstack = inventoryContents(slot).splitStack(amount)
        if (inventoryContents(slot).stackSize == 0) {
          inventoryContents(slot) = null
        }
        onInventoryChanged(slot)
        itemstack
      }
    } else null
  }
  override def getInventoryStackLimit(): Int = 64
  override def getSizeInventory(): Int
  override def getStackInSlot(slot: Int): ItemStack = inventoryContents(slot)
  override def getStackInSlotOnClosing(slot: Int): ItemStack = {
    if (inventoryContents(slot) != null) {
      val itemstack = inventoryContents(slot)
      inventoryContents(slot) = null
      itemstack
    } else null
  }
  def hasStackInSlot(slot: Int): Boolean = getStackInSlot(slot) != null
  override def isItemValidForSlot(slot: Int, itemstack: ItemStack): Boolean = false
  override def isUseableByPlayer(player: EntityPlayer): Boolean = if (getWorld.getTileEntity(new BlockPos(getPos.getX, getPos.getY, getPos.getZ)) != this) false else player.getDistanceSq(getPos.getX + 0.5D, getPos.getY + 0.5D, getPos.getZ + 0.5D) <= 64.0D
  override def readFromNBT(tag: NBTTagCompound) {
    inventoryContents = Array.ofDim[ItemStack](getSizeInventory)
    val inventory = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND)
    for (i <- 0 until inventory.tagCount()) {
      val Slots = inventory.getCompoundTagAt(i).asInstanceOf[NBTTagCompound]
      val slot = Slots.getByte("Slot")
      if ((slot >= 0) && (slot < inventoryContents.length)) inventoryContents(slot) = ItemStack.loadItemStackFromNBT(Slots)
    }
    super.readFromNBT(tag)
  }
  def onInventoryChanged(slot: Int) {

  }
  override def setInventorySlotContents(slot: Int, stack: ItemStack) {
    inventoryContents(slot) = stack
    if ((stack != null) && (stack.stackSize > getInventoryStackLimit)) stack.stackSize = getInventoryStackLimit
    onInventoryChanged(slot)
  }
  override def writeToNBT(tag: NBTTagCompound) {
    val inventory = new NBTTagList()
    for (slot <- 0 until inventoryContents.length if inventoryContents(slot) != null) {
      val Slots = new NBTTagCompound()
      Slots.setByte("Slot", slot.toByte)
      inventoryContents(slot).writeToNBT(Slots)
      inventory.appendTag(Slots)
    }
    tag.setTag("Items", inventory)
    super.writeToNBT(tag)
  }
  override def openInventory(player:EntityPlayer) {}
  override def closeInventory(player:EntityPlayer) {}
  override def getName: String = "container_" + getBlockType.getUnlocalizedName.substring(5)
  override def hasCustomName: Boolean = false
  override def getDisplayName: IChatComponent = new ChatComponentText(getName)
  override def getField(id: Int): Int = 0
  override def getFieldCount(): Int = 0
  override def setField(id: Int, value: Int) {}
  override def clear = for(i <- 0 until inventoryContents.length) inventoryContents(i) = null
}