package com.rikmuld.corerm.objs

import com.rikmuld.corerm.network.PacketSender
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.network.NetworkManager
import net.minecraft.network.Packet
import com.rikmuld.corerm.network.TileData
import net.minecraft.init.{Blocks, Items}
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.network.play.server.SPacketUpdateTileEntity

class RMTile extends TileEntity {
  override def readFromNBT(tag: NBTTagCompound) = super.readFromNBT(tag)
  override def writeToNBT(tag: NBTTagCompound) = super.writeToNBT(tag)
  override def getUpdatePacket(): SPacketUpdateTileEntity = {
    val compound = getUpdateTag
    new SPacketUpdateTileEntity(pos, 1, compound)
  }
  def bd = (world, pos)
  override def onDataPacket(net: NetworkManager, packet: SPacketUpdateTileEntity) {
    super.onDataPacket(net, packet)
    readFromNBT(packet.getNbtCompound());
  }
  def setTileData(id: Int, data: Array[Int]) {}
  def sendTileData(id: Int, toClient: Boolean, data: Int*) {
    if (!toClient && world.isRemote) PacketSender.toServer(new TileData(id, pos.getX, pos.getY, pos.getZ, data));
    else if (toClient && !world.isRemote) PacketSender.toClient(new TileData(id, pos.getX, pos.getY, pos.getZ, data));
  }
  override def getUpdateTag(): NBTTagCompound = {
    val tag = super.getUpdateTag()
    this.writeToNBT(tag)
  }
}

abstract trait WithTileInventory extends TileEntity with IInventory {
  var inventoryContents: Array[ItemStack] = new Array[ItemStack](getSizeInventory)

  override def decrStackSize(slot: Int, amount: Int): ItemStack = {
    if (inventoryContents(slot) != null) {
      var itemstack: ItemStack = null
      if (inventoryContents(slot).getCount <= amount) {
        itemstack = inventoryContents(slot)
        inventoryContents(slot) = null
        onInventoryChanged(slot)
        itemstack
      } else {
        itemstack = inventoryContents(slot).splitStack(amount)
        if (inventoryContents(slot).getCount == 0) {
          inventoryContents(slot) = null
        }
        onInventoryChanged(slot)
        itemstack
      }
    } else null
  }

  override def isEmpty: Boolean = {
    for (i <- 0 until getSizeInventory){
      if(getStackInSlot(i).getCount > 0) return false
    }
    true
  }
  override def getInventoryStackLimit(): Int = 64
  override def getSizeInventory(): Int
  override def getStackInSlot(slot: Int): ItemStack = inventoryContents(slot) match {
    case null => new ItemStack(Items.AIR)
    case someStack => someStack
  }
  override def removeStackFromSlot(slot: Int): ItemStack = {
    if (inventoryContents(slot) != null) {
      val itemstack = inventoryContents(slot)
      inventoryContents(slot) = null
      itemstack
    } else null
  }
  def hasStackInSlot(slot: Int): Boolean = getStackInSlot(slot) != null
  override def isItemValidForSlot(slot: Int, itemstack: ItemStack): Boolean = false
  override def isUsableByPlayer(player: EntityPlayer): Boolean = if (getWorld.getTileEntity(new BlockPos(getPos.getX, getPos.getY, getPos.getZ)) != this) false else player.getDistanceSq(getPos.getX + 0.5D, getPos.getY + 0.5D, getPos.getZ + 0.5D) <= 64.0D
  override def readFromNBT(tag: NBTTagCompound) {
    inventoryContents = Array.ofDim[ItemStack](getSizeInventory)
    val inventory = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND)
    for (i <- 0 until inventory.tagCount()) {
      val Slots = inventory.getCompoundTagAt(i).asInstanceOf[NBTTagCompound]
      val slot = Slots.getByte("Slot")
      if ((slot >= 0) && (slot < inventoryContents.length)) inventoryContents(slot) = new ItemStack(Slots)
    }
    super.readFromNBT(tag)
  }
  def onInventoryChanged(slot: Int) {

  }
  override def setInventorySlotContents(slot: Int, stack: ItemStack) {
    inventoryContents(slot) = stack
    if ((stack != null) && (stack.getCount > getInventoryStackLimit)) stack.setCount(getInventoryStackLimit)
    onInventoryChanged(slot)
  }
  override def writeToNBT(tag: NBTTagCompound):NBTTagCompound = {
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
  override def getDisplayName: ITextComponent = new TextComponentString(getName)
  override def getField(id: Int): Int = 0
  override def getFieldCount(): Int = 0
  override def setField(id: Int, value: Int) {}
  override def clear = for(i <- 0 until inventoryContents.length) inventoryContents(i) = null
}