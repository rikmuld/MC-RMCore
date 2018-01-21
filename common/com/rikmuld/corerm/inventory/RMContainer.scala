package com.rikmuld.corerm.inventory

import net.minecraft.inventory.IInventory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.inventory.Container
import net.minecraft.item.Item
import net.minecraft.inventory.Slot

abstract class RMContainerItem(player: EntityPlayer) extends Container {
  val invPlayer: InventoryPlayer = player.inventory
  val inv = getItemInv

  inv.openInventory(player)

  override def canInteractWith(player: EntityPlayer): Boolean = !player.isDead && inv.isUsableByPlayer(player)
  override def onContainerClosed(player: EntityPlayer) {
    super.onContainerClosed(player)
    inv.closeInventory(player)
    
    val item = player.inventory.getCurrentItem
    
    if (item != null && item.getItem().equals(getItem) && ( getItemDamage==(-1) || item.getItemDamage == getItemDamage)) {
      inv.setNBT(item)
    }
  }
  override def transferStackInSlot(p: EntityPlayer, i: Int): ItemStack = {
    var itemstack: ItemStack = ItemStack.EMPTY
    var slot = inventorySlots.get(i)
    if ((slot != null) && slot.getHasStack()) {
      var itemstack1 = slot.getStack()
      itemstack = itemstack1.copy()
      if (i < inv.getSizeInventory()) {
        if (!mergeItemStack(itemstack1, inv.getSizeInventory(), inventorySlots.size(), false)) return ItemStack.EMPTY
      } else if (!mergeItemStack(itemstack1, 0, inv.getSizeInventory(), false)) {
        if(i < inv.getSizeInventory + 9){
          if(!mergeItemStack(itemstack1, inv.getSizeInventory + 9, inv.getSizeInventory + 9 + 27, false)) return ItemStack.EMPTY
        } else {
          if(!mergeItemStack(itemstack1, inv.getSizeInventory, inv.getSizeInventory + 9, false)) return ItemStack.EMPTY
        }
      }
      if (itemstack1.getCount == 0) slot.putStack(new ItemStack(Items.AIR, 0))
      else slot.onSlotChanged()
    }
    itemstack
  }
 
  def getItemInv: RMInventoryItem
  def getItem: Item
  def getItemDamage: Int = -1
}

abstract class RMContainerTile(player: EntityPlayer, tile: IInventory) extends Container {
  override def canInteractWith(player: EntityPlayer): Boolean = !player.isDead && tile!=null && tile.isUsableByPlayer(player)
  override def onContainerClosed(player: EntityPlayer) {
    super.onContainerClosed(player)
    if(tile!=null)tile.closeInventory(player)
  }
  override def transferStackInSlot(player: EntityPlayer, slotNum: Int): ItemStack = {
    var itemstack: ItemStack = ItemStack.EMPTY
    val slot = inventorySlots.get(slotNum).asInstanceOf[Slot]
    if ((slot != null) && slot.getHasStack) {
      val itemstack1 = slot.getStack
      itemstack = itemstack1.copy()
      if (slotNum < tile.getSizeInventory) {
        if (!mergeItemStack(itemstack1, tile.getSizeInventory, inventorySlots.size, true)) return ItemStack.EMPTY
      } else if (!mergeItemStack(itemstack1, 0, tile.getSizeInventory(), false)) return ItemStack.EMPTY
      if (itemstack1.getCount == 0) slot.putStack(new ItemStack(Items.AIR, 0))
      else slot.onSlotChanged()
    }
    itemstack
  }
}