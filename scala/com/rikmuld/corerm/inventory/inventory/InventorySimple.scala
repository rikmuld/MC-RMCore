package com.rikmuld.corerm.inventory.inventory

import com.rikmuld.corerm.utils.StackHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.text.{ITextComponent, TextComponentString}

trait InventorySimple extends IInventory {
  private var inventoryContents: Seq[ItemStack] =
    Vector.fill(getSizeInventory)(ItemStack.EMPTY)

  def getInventory: Seq[ItemStack] =
    inventoryContents

  def getContents: Seq[ItemStack] =
    getInventory.filter(stack => !stack.isEmpty)

  protected def setInventory(inventory: Seq[ItemStack]): Unit =
    inventoryContents = inventory

  def changeInventory(inventory: Seq[ItemStack]): Unit = {
    markDirty()
    setInventory(inventory)
  }

  def isEmpty: Boolean =
    getInventory.forall(_.isEmpty)

  def getStackInSlot(index: Int): ItemStack =
    getInventory(index)

  def decrStackSize(index: Int, count: Int): ItemStack = {
    val stack = getStackInSlot(index)

    if(stack.isEmpty) ItemStack.EMPTY
    else if(stack.getCount < count) {
      setInventorySlotContents(index, ItemStack.EMPTY)
      stack
    } else {
      val split = stack.splitStack(count)
      setInventorySlotContents(index, stack)
      split
    }
  }

  def removeStackFromSlot(index: Int): ItemStack = getStackInSlot(index) match {
    case stack if stack.isEmpty => ItemStack.EMPTY
    case stack =>
      setInventorySlotContents(index, ItemStack.EMPTY)
      stack
  }

  def setInventorySlotContents(index: Int, stack: ItemStack): Unit = {
    changeInventory(
      if (stack == null) getInventory.updated(index, ItemStack.EMPTY)
      else getInventory.updated(index, StackHelper.limit(stack, getInventoryStackLimit))
    )
    onChange(index)
  }

  def onChange(slot: Int): Unit =
    Unit

  def getInventoryStackLimit: Int =
    64

  def isItemValidForSlot(index: Int, stack: ItemStack): Boolean =
    false

  def getField(id: Int): Int =
    0

  def setField(id: Int, value: Int): Unit =
    Unit

  def getFieldCount: Int =
    0

  def clear(): Unit =
    changeInventory(Seq.fill(getSizeInventory)(ItemStack.EMPTY))

  def getName: String =
    ""

  def hasCustomName: Boolean =
    false

  def getDisplayName: ITextComponent =
    new TextComponentString(getName)
}