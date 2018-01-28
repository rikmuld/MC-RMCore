package com.rikmuld.corerm.inventory.gui.buttons

import net.minecraft.client.gui.GuiButton

trait ButtonTabbed extends GuiButton {
  val tab: Int

  def updateTab(index: Int): Unit =
    visible = tab == index
}