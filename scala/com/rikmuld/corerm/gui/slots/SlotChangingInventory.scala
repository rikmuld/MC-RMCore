package com.rikmuld.corerm.gui.slots

import net.minecraft.inventory.{IInventory, Slot}
import net.minecraft.item.ItemStack

trait SlotChangingInventory extends Slot {
  def getIInventory: Option[IInventory]

  def setIInventory(inv: Option[IInventory]): Unit

  override def getStack: ItemStack =
    getIInventory.fold(ItemStack.EMPTY)(_.getStackInSlot(getSlotIndex))

  override def putStack(stack: ItemStack): Unit =
    getIInventory.foreach { inv =>
      inv.setInventorySlotContents(getSlotIndex, stack)
      inv.markDirty()
    }

  override def onSlotChanged(): Unit =
    getIInventory.foreach(_.markDirty())

  override def getSlotStackLimit: Int =
    getIInventory.fold(0)(_.getInventoryStackLimit)

  override def decrStackSize(amount: Int): ItemStack =
    getIInventory.fold(ItemStack.EMPTY)(_.decrStackSize(getSlotIndex, amount))

  override def isHere(other: IInventory, slotIn: Int): Boolean =
    getIInventory.fold(false)(inv => (other eq inv) && (slotIn == getSlotIndex))

  override def isSameInventory(other: Slot): Boolean =
    getIInventory.fold(false)(inv => other match {
      case slot: SlotChangingInventory => slot.getIInventory.fold(false)(inv eq _)
      case slot => inv eq slot.inventory
    })
}