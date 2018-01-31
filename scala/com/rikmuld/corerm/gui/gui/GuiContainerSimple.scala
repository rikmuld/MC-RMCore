package com.rikmuld.corerm.gui.gui

import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

abstract class GuiContainerSimple(container: Container) extends GuiContainer(container) {
  val getTexture: ResourceLocation

  protected override def drawGuiContainerBackgroundLayer(partialTick: Float, mouseX: Int, mouseY: Int) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
    bindTexture()
    drawGUI(mouseX, mouseY)
  }

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    this.drawDefaultBackground()
    super.drawScreen(mouseX, mouseY, partialTicks)
    this.renderHoveredToolTip(mouseX, mouseY)
  }

  def bindTexture(): Unit =
    mc.renderEngine.bindTexture(getTexture)

  def drawGUI(mouseX: Int, mouseY: Int): Unit =
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)

  override def drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int): Unit =
    if (hasName) drawCenteredString(fontRenderer, getName, xSize / 2, 8, 4210752)

  override def drawCenteredString(fontRender: FontRenderer, text: String, x: Int, y: Int, color: Int): Unit =
    fontRender.drawString(text, x - fontRender.getStringWidth(text) / 2, y, color)

  def getName: String =
    ""

  def hasName: Boolean =
    false
}