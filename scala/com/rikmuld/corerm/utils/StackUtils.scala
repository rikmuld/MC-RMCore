package com.rikmuld.corerm.utils

import net.minecraft.item.ItemStack

object StackUtils {
  def limit(stack: ItemStack, max: Int): ItemStack = {
    if (stack.getCount > max) stack.setCount(max)

    stack
  }

  //TODO
  def flatten(stacks: Seq[ItemStack]): Seq[ItemStack] = {
    val tuples = for(stack <- stacks if !stack.isEmpty)
      yield (stack.getItem, stack.getItemDamage) -> stack.getCount

    tuples.groupBy(_._1).mapValues(_.map(_._2).sum).map{
      case((item, damage), size) =>
        new ItemStack(item, size, damage)
    }.toSeq
  }
}