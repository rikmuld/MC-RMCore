package com.rikmuld.corerm.gui.container

import com.rikmuld.corerm.gui.slots.SlotNoPickup
import com.rikmuld.corerm.inventory.InventoryItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot

abstract class ContainerItem(player: EntityPlayer) extends ContainerSimple[InventoryItem](player) {
  override def addPlayerSlots(): Unit = {
    for (row <- 0 until 9) {
      addSlotToContainer(
        if (row == player.inventory.currentItem)
          new Slot(player.inventory, row, playerInvX + (row * 18), playerInvY + 58) with SlotNoPickup
        else
          new Slot(player.inventory, row, playerInvX + (row * 18), playerInvY + 58)
      )
    }

    addSlots(player.inventory, 9, 3, 9, playerInvX, playerInvY)
  }

  def getID: String =
    player.inventory.getCurrentItem.getItem.getRegistryName.getResourcePath
}

