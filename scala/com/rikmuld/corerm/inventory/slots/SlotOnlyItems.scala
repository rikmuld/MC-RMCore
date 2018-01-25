package com.rikmuld.corerm.inventory.slots

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

trait SlotOnlyItems extends Slot {
  override def isItemValid(stack: ItemStack): Boolean =
    Block.getBlockFromItem(stack.getItem) == Blocks.AIR
}



