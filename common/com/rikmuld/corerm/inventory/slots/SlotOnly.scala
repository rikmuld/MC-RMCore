package com.rikmuld.corerm.inventory.slots

import net.minecraft.inventory.Slot
import net.minecraft.item.{Item, ItemStack}

trait SlotOnly extends Slot {
  def getAllowedItems: Vector[Item] =
    Vector()

  def getAllowedStacks: Vector[ItemStack] =
    Vector()

  override def isItemValid(stack: ItemStack): Boolean =
    getAllowedItems.contains(stack.getItem) || getAllowedStacks.exists(test =>
      stack.getItem == test.getItem && stack.getItemDamage == test.getItemDamage
    )
}
