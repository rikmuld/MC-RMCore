package com.rikmuld.corerm.inventory

import net.minecraft.inventory.IInventory
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.item.Item
import net.minecraftforge.common.util.Constants
import net.minecraft.util.IChatComponent
import net.minecraft.util.ChatComponentText

class RMPlayerInventory(player: EntityPlayer, size: Int) extends IInventory {
  private var inventoryContents: Array[ItemStack] = new Array[ItemStack](size)
  var tag: NBTTagCompound = _
  var name: String = player.getName

  override def closeInventory(player:EntityPlayer) = writeToNBT(tag)
  override def decrStackSize(slot: Int, amount: Int): ItemStack = {
    if (inventoryContents(slot) != null) {
      var itemstack: ItemStack = null
      if (inventoryContents(slot).stackSize <= amount) {
        itemstack = inventoryContents(slot)
        inventoryContents(slot) = null
        onChange(slot)
        itemstack
      } else {
        itemstack = inventoryContents(slot).splitStack(amount)
        if (inventoryContents(slot).stackSize == 0) {
          inventoryContents(slot) = null
        }
        onChange(slot)
        itemstack
      }
    } else null
  }
  def getInventory = inventoryContents
  override def getInventoryStackLimit(): Int = 64
  override def getSizeInventory(): Int = inventoryContents.length
  override def getStackInSlot(slot: Int): ItemStack = inventoryContents(slot)
  override def removeStackFromSlot(slot: Int): ItemStack = {
    if (inventoryContents(slot) != null) {
      val itemstack = inventoryContents(slot)
      inventoryContents(slot) = null
      itemstack
    } else null
  }
  override def isItemValidForSlot(slot: Int, itemstack: ItemStack): Boolean = true
  override def isUseableByPlayer(player: EntityPlayer): Boolean = true
  override def openInventory(player:EntityPlayer) = readFromNBT(tag)
  def readFromNBT(tag: NBTTagCompound) {
    inventoryContents = Array.ofDim[ItemStack](getSizeInventory)
    val inventory = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND)
    for (i <- 0 until inventory.tagCount()) {
      val Slots = inventory.getCompoundTagAt(i).asInstanceOf[NBTTagCompound]
      val slot = Slots.getByte("Slot")
      if ((slot >= 0) && (slot < inventoryContents.length)) {
        inventoryContents(slot) = ItemStack.loadItemStackFromNBT(Slots)
      }
    }
  }
  override def setInventorySlotContents(slot: Int, stack: ItemStack) {
    inventoryContents(slot) = stack
    if ((stack != null) && (stack.stackSize > getInventoryStackLimit)) {
      stack.stackSize = getInventoryStackLimit
    }
    onChange(slot)
  }
  def onChange(slotNum: Int) = writeToNBT(tag)
  def writeToNBT(tag: NBTTagCompound) {
    val inventory = new NBTTagList()
    for (slot <- 0 until inventoryContents.length if inventoryContents(slot) != null) {
      val Slots = new NBTTagCompound()
      Slots.setByte("Slot", slot.toByte)
      inventoryContents(slot).writeToNBT(Slots)
      inventory.appendTag(Slots)
    }
    tag.setTag("Items", inventory)
  }
  override def markDirty() {}
  override def getName: String = "customContainer_player"
  override def hasCustomName: Boolean = false
  override def getDisplayName: IChatComponent = new ChatComponentText(getName)
  override def getField(id: Int): Int = 0
  override def getFieldCount(): Int = 0
  override def setField(id: Int, value: Int) {}
  override def clear = for(i <- 0 until inventoryContents.length) inventoryContents(i) = null
}

class RMInventoryItem(stack: ItemStack, var player: EntityPlayer, size: Int, var stackSize: Int, toEquipped:Boolean) extends IInventory {
  if (stack.hasTagCompound() == false) stack.setTagCompound(new NBTTagCompound())

  private var inventoryContents: Array[ItemStack] = new Array[ItemStack](size)
  var tag: NBTTagCompound = stack.getTagCompound
  var name: String = stack.getUnlocalizedName
  var item: Item = stack.getItem

  override def decrStackSize(slot: Int, amount: Int): ItemStack = {
    if (inventoryContents(slot) != null) {
      var itemstack: ItemStack = null
      if (inventoryContents(slot).stackSize <= amount) {
        itemstack = inventoryContents(slot)
        inventoryContents(slot) = null
        this.writeToNBT(tag)
        if(player.getCurrentEquippedItem != null&&player.getCurrentEquippedItem.getItem == item&&toEquipped) this.setNBT(player.getCurrentEquippedItem)
        itemstack
      } else {
        itemstack = inventoryContents(slot).splitStack(amount)
        if (inventoryContents(slot).stackSize == 0) {
          inventoryContents(slot) = null
        }
        this.writeToNBT(tag)
        if(player.getCurrentEquippedItem != null&&player.getCurrentEquippedItem.getItem == item&&toEquipped) this.setNBT(player.getCurrentEquippedItem)
        itemstack
      }
    } else {
      this.writeToNBT(tag)
      if(player.getCurrentEquippedItem != null&&player.getCurrentEquippedItem.getItem == item&&toEquipped) this.setNBT(player.getCurrentEquippedItem)
      null
    }
  }
  override def getInventoryStackLimit(): Int = stackSize
  override def getSizeInventory(): Int = inventoryContents.length
  override def getStackInSlot(slot: Int): ItemStack = inventoryContents(slot)
  override def removeStackFromSlot(slot: Int): ItemStack = {
    if (inventoryContents(slot) != null) {
      val itemstack = inventoryContents(slot)
      inventoryContents(slot) = null
      itemstack
    } else null
  }
  override def isItemValidForSlot(slot: Int, itemstack: ItemStack): Boolean = true
  override def isUseableByPlayer(player: EntityPlayer): Boolean = {
    player.getCurrentEquippedItem != null && player.getCurrentEquippedItem.getItem == item
  }
  def readFromNBT(tag: NBTTagCompound) {
    inventoryContents = Array.ofDim[ItemStack](getSizeInventory)
    val inventory = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND)
    for (i <- 0 until inventory.tagCount()) {
      val Slots = inventory.getCompoundTagAt(i).asInstanceOf[NBTTagCompound]
      val slot = Slots.getByte("Slot")
      if ((slot >= 0) && (slot < inventoryContents.length)) {
        inventoryContents(slot) = ItemStack.loadItemStackFromNBT(Slots)
      }
    }
  }
  override def setInventorySlotContents(slot: Int, stack: ItemStack) {
    inventoryContents(slot) = stack
    if ((stack != null) && (stack.stackSize > getInventoryStackLimit)) {
      stack.stackSize = getInventoryStackLimit
    }
    this.writeToNBT(tag)
    if (player.getCurrentEquippedItem != null&&player.getCurrentEquippedItem.getItem == item&&toEquipped) this.setNBT(player.getCurrentEquippedItem)
  }
  def setNBT(item: ItemStack) = item.setTagCompound(tag)
  def writeToNBT(tag: NBTTagCompound) {    
    val inventory = new NBTTagList()
    for (slot <- 0 until inventoryContents.length if inventoryContents(slot) != null) {
      val Slots = new NBTTagCompound()
      Slots.setByte("Slot", slot.toByte)
      inventoryContents(slot).writeToNBT(Slots)
      inventory.appendTag(Slots)
    }
    tag.setTag("Items", inventory)
  }
  override def closeInventory(player:EntityPlayer) = writeToNBT(tag)
  override def openInventory(player:EntityPlayer) = readFromNBT(tag)
  override def markDirty() {}
  override def getName: String = "container_" + stack.getUnlocalizedName
  override def hasCustomName: Boolean = false
  override def getDisplayName: IChatComponent = new ChatComponentText(getName)
  override def getField(id: Int): Int = 0
  override def getFieldCount(): Int = 0
  override def setField(id: Int, value: Int) {}
  override def clear = for(i <- 0 until inventoryContents.length) inventoryContents(i) = null
}