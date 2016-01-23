package com.rikmuld.corerm.inventory

import net.minecraft.inventory.IInventory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.inventory.Container
import net.minecraft.item.Item
import net.minecraft.inventory.Slot

abstract class RMContainerItem(player: EntityPlayer) extends Container {
  val invPlayer: InventoryPlayer = player.inventory
  val inv = getItemInv

  inv.openInventory(player)

  override def canInteractWith(player: EntityPlayer): Boolean = !player.isDead && inv.isUseableByPlayer(player)
  override def onContainerClosed(player: EntityPlayer) {
    super.onContainerClosed(player)
    inv.closeInventory(player)
    if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem().equals(getItem)) ((inv).setNBT(player.getCurrentEquippedItem()))
  }
  override def transferStackInSlot(p: EntityPlayer, i: Int): ItemStack = {
    var itemstack: ItemStack = null
    var slot = inventorySlots.get(i).asInstanceOf[Slot]
    if ((slot != null) && slot.getHasStack()) {
      var itemstack1 = slot.getStack()
      itemstack = itemstack1.copy()
      if (i < inv.getSizeInventory()) {
        if (!mergeItemStack(itemstack1, inv.getSizeInventory(), inventorySlots.size(), true)) return null
      } else if (!mergeItemStack(itemstack1, 0, inv.getSizeInventory(), false)) return null
      if (itemstack1.stackSize == 0) slot.putStack(null)
      else slot.onSlotChanged()
    }
    itemstack
  }
  def getItemInv: RMInventoryItem
  def getItem: Item
}

abstract class RMContainerTile(player: EntityPlayer, tile: IInventory) extends Container {
  override def canInteractWith(player: EntityPlayer): Boolean = !player.isDead && tile.isUseableByPlayer(player)
  override def onContainerClosed(player: EntityPlayer) {
    super.onContainerClosed(player)
    tile.closeInventory(player)
  }
  override def transferStackInSlot(player: EntityPlayer, slotNum: Int): ItemStack = {
    var itemstack: ItemStack = null
    val slot = inventorySlots.get(slotNum).asInstanceOf[Slot]
    if ((slot != null) && slot.getHasStack) {
      val itemstack1 = slot.getStack
      itemstack = itemstack1.copy()
      if (slotNum < tile.getSizeInventory) {
        if (!mergeItemStack(itemstack1, tile.getSizeInventory, inventorySlots.size, true)) return null
      } else if (!mergeItemStack(itemstack1, 0, tile.getSizeInventory(), false)) return null
      if (itemstack1.stackSize == 0) {
        slot.putStack(null)
      } else {
        slot.onSlotChanged()
      }
    }
    itemstack
  }
}