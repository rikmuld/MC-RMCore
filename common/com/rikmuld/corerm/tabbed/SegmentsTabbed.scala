package com.rikmuld.corerm.tabbed

import net.minecraft.client.gui.GuiButton
import net.minecraft.inventory.IInventory
import net.minecraft.entity.player.EntityPlayer
import com.rikmuld.corerm.inventory.SlotWithItemsNot
import net.minecraft.inventory.SlotCrafting
import net.minecraft.inventory.InventoryCrafting
import com.rikmuld.corerm.inventory.SlotWithDisable
import net.minecraft.inventory.Slot

trait ButtonWithTab extends GuiButton {
  visible = false
  
  var left = 0
  var top = 0
  
  def setTab(left:Int, top:Int){
    this.left = left
    this.top = top
  }
  def tabChanged(tabLeft:Int, tabTop:Int){
    if(left==tabLeft&&top==tabTop){
      if(!visible)visible = true
    } else if(visible)visible = false
  }
}

class ButtonTabbed(id:Int, x:Int, y:Int, width:Int, height:Int, text:String, top:Int, left:Int) extends GuiButton(id, x, y, width, height, text) with ButtonWithTab {
  setTab(left, top)
}

trait SlotWithTabs extends SlotWithDisable {
  disable
  var canEnable:Boolean = _
  var tabIdLeft:Int = _
  var tabIdTop:Int = _
  def setTabIds(top:Int, left:Int){
    this.tabIdLeft = left
    this.tabIdTop = top
  }
  def updateTab(player:EntityPlayer, idLeft:Int, idTop:Int){
    if(tabIdLeft==idLeft&&tabIdTop==idTop){
      canEnable = true
      if(!enabled)enable
    } else {
      canEnable = false
      if(enabled)disable
    }
  }
  override def enable = if(canEnable)super.enable
}

class SlotTabbed(inv: IInventory, id: Int, xFlag: Int, yFlag: Int, tabIdTop:Int, tabIdLeft:Int) extends Slot(inv, id, xFlag, yFlag) with SlotWithTabs {
  setTabIds(tabIdTop, tabIdLeft)
  setDisableSlot(xFlag, yFlag)
}

class SlotTabbedItemsNot(inv: IInventory, id: Int, xFlag: Int, yFlag: Int, tabIdTop:Int, tabIdLeft:Int, stacks: AnyRef*) extends Slot(inv, id, xFlag, yFlag) with SlotWithItemsNot with SlotWithTabs {
  setTabIds(tabIdTop, tabIdLeft)
  setDisableSlot(xFlag, yFlag)
  setStacks(stacks)
}

class SlotTabbedCrafting(player:EntityPlayer, craft:InventoryCrafting, inv: IInventory, id: Int, x: Int, y: Int, tabIdTop:Int, tabIdLeft:Int) extends SlotCrafting(player, craft, inv, id, x, y) with SlotWithTabs {
  setTabIds(tabIdTop, tabIdLeft)
  setDisableSlot(x, y)
}