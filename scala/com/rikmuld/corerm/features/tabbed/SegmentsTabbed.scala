package com.rikmuld.corerm.features.tabbed

import com.rikmuld.corerm.inventory.slots.SlotDisable
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.EntityPlayer

trait ButtonWithTab extends GuiButton {
  visible = false
  
  val left: Int

  val top: Int

  def tabChanged(tabLeft:Int, tabTop:Int){
    if(left==tabLeft&&top==tabTop){
      if(!visible) visible = true
    } else if(visible) visible = false
  }
}

trait SlotWithTabs extends SlotDisable {
  disable()

  var canEnable:Boolean = _

  val tabIdLeft:Int

  val tabIdTop:Int

  def updateTab(player:EntityPlayer, idLeft:Int, idTop:Int){
    if(tabIdLeft == idLeft && tabIdTop == idTop){
      canEnable = true
      if(!enabled) enable()
    } else {
      canEnable = false
      if(enabled) disable()
    }
  }

  override def enable() =
    if(canEnable)
      super.enable()
}