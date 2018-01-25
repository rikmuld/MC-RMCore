package com.rikmuld.corerm.utils

import net.minecraft.item.ItemStack

object StackHelper {
  def limit(stack: ItemStack, max: Int): ItemStack = {
    if (stack.isEmpty) ItemStack.EMPTY
    else if (stack.getCount > max) stack.setCount(max)

    stack
  }
}