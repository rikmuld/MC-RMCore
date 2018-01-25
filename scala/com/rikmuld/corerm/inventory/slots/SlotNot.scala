package com.rikmuld.corerm.inventory.slots

import net.minecraft.inventory.Slot
import net.minecraft.item.{Item, ItemStack}

trait SlotNot extends Slot {
  def getBanItems: Vector[Item] =
    Vector()

  def getBanStacks: Vector[ItemStack] =
    Vector()

  override def isItemValid(stack: ItemStack): Boolean =
    !getBanItems.contains(stack.getItem) && getBanStacks.forall(test =>
      stack.getItem != test.getItem || stack.getItemDamage != test.getItemDamage
    )
}
