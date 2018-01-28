package com.rikmuld.corerm.gui.gui.buttons

import net.minecraft.client.gui.GuiButton

trait ButtonTabbed extends GuiButton {
  val tab: Int

  def updateTab(index: Int): Unit =
    visible = tab == index
}