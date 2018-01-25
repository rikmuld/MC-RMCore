package com.rikmuld.corerm.inventory.slots

import net.minecraft.inventory.Slot

trait SlotDisable extends Slot {
  var enabled:Boolean = true

  val xFlag:Int = xPos
  val yFlag:Int = yPos

  def disable() = {
    xPos = -500
    yPos = -500

    enabled = false
  }

  def enable() = {
    xPos = xFlag
    yPos = yFlag

    enabled = true
  }
}
