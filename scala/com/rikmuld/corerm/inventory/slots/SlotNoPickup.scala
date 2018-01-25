package com.rikmuld.corerm.inventory.slots

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot

trait SlotNoPickup extends Slot {
  override def canTakeStack(par1EntityPlayer: EntityPlayer): Boolean =
    false
}
