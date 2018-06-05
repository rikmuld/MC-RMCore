package com.rikmuld.corerm.gui.slots

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot

trait SlotNoPickup extends Slot {
  override def canTakeStack(player: EntityPlayer): Boolean =
    false
}
