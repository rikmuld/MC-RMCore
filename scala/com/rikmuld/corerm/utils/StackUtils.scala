package com.rikmuld.corerm.utils

import net.minecraft.item.ItemStack

object StackUtils {
  def limit(stack: ItemStack, max: Int): ItemStack = {
    if (stack.getCount > max) stack.setCount(max)

    stack
  }
}