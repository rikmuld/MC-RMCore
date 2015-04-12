package com.rikmuld.corerm.common.inventory

import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import java.util.ArrayList
import net.minecraft.item.Item
import scala.collection.mutable.ListBuffer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import scala.collection.mutable.WrappedArray
import com.rikmuld.corerm.core.CoreUtils.WrappedArrayUtils

trait SlotWithNoPickup extends Slot {
  override def canTakeStack(par1EntityPlayer: EntityPlayer): Boolean = false
}

class SlotNoPickup(inv: IInventory, id: Int, x: Int, y: Int) extends Slot(inv, id, x, y) with SlotWithNoPickup {}

trait SlotWithItemsNot extends Slot {
  var alowedStacks: ListBuffer[Any] = new ListBuffer[Any]()

  def setStacks(stacks: AnyRef*) = for (stack <- stacks) alowedStacks.append(stack)
  def setStacks(stacks:Set[Any]) = for (id <- stacks) alowedStacks.append(id)
  override def isItemValid(is: ItemStack): Boolean = {
    var flag = false
    println(is)
    alowedStacks.foreach(st => {
      val stack = if(st.isInstanceOf[WrappedArray[_]]) st.asInstanceOf[WrappedArray[_]](0) else st
      if (stack.isInstanceOf[ItemStack] && !stack.asInstanceOf[ItemStack].isItemEqual(is)) flag = true 
      else if (stack.isInstanceOf[Item] && !is.getItem.equals(stack)) flag = true
    })
    if (alowedStacks.size == 0) true else flag == true
  }
}

class SlotItemsNot(inv: IInventory, id: Int, x: Int, y: Int, stacks: Any*) extends Slot(inv, id, x, y) with SlotWithItemsNot {
  setStacks(stacks)
}

trait SlotWithItemsOnly extends Slot {
  var alowedStacks: ListBuffer[Any] = new ListBuffer[Any]()

  def setStacks(stacks: Any*) {
    for (stack <- stacks) {
      if(stack.isInstanceOf[WrappedArray[_]]) {
        for(st <- stack.asInstanceOf[WrappedArray[_]].unwrap)alowedStacks.append(st)
      }
      else alowedStacks.append(stack)
    }
  }
  def setStacks(stacks:Set[Any]) = for (id <- stacks) alowedStacks.append(id)
  override def isItemValid(is: ItemStack): Boolean = {
    var flag = false
    for (st <- alowedStacks) {
      println(st)
      
      if (st.isInstanceOf[ItemStack] && st.asInstanceOf[ItemStack].isItemEqual(is)) flag = true
      else if (st.isInstanceOf[Item] && is.getItem.equals(st)) flag = true
    }
    if (alowedStacks.size == 0) true else flag == true
  }
}

class SlotItemsOnly(inv: IInventory, id: Int, x: Int, y: Int, stacks: AnyRef*) extends Slot(inv, id, x, y) with SlotWithItemsOnly {
  setStacks(stacks)
}

trait SlotWithOnlyItem extends Slot {
  override def isItemValid(stack: ItemStack): Boolean = Block.getBlockFromItem(stack.getItem()) == Blocks.air
}

class SlotOnlyItems(inv: IInventory, id: Int, x: Int, y: Int) extends Slot(inv, id, x, y) with SlotWithOnlyItem {}

trait SlotWithOnlyBlock extends Slot {
  override def isItemValid(stack: ItemStack): Boolean = Block.getBlockFromItem(stack.getItem()) != Blocks.air
}

class SlotOnlyBlocks(inv: IInventory, id: Int, x: Int, y: Int) extends Slot(inv, id, x, y) with SlotWithOnlyBlock {}

trait SlotWithDisable extends Slot {
  var enabled:Boolean = _
  var xFlag:Int = _
  var yFlag:Int = _
  
  def setDisableSlot(x:Int, y:Int){
    xFlag = x;
    yFlag = y;
  }
  def disable = {
    xDisplayPosition = -500
    yDisplayPosition = -500
    
    enabled = false
  }
  def enable = {
    xDisplayPosition = xFlag
    yDisplayPosition = yFlag
    
    enabled = true
  }
}

class SlotDisable(inv: IInventory, id: Int, x: Int, y: Int) extends Slot(inv, id, x, y) with SlotWithDisable {
  setDisableSlot(x, y)
}