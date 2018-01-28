package com.rikmuld.corerm.inventory.slots

import net.minecraft.entity.player.EntityPlayer

trait SlotTabbed extends SlotDisable {
  disable()

  var canEnable =
    false

  val tab: Int

  def updateTab(player:EntityPlayer, index:Int): Unit =
    if(tab == index) {
      canEnable = true
      if(!enabled) enable()
    } else {
      canEnable = false
      if(enabled) disable()
    }

  override def enable(): Unit =
    if(canEnable)
      super.enable()
}