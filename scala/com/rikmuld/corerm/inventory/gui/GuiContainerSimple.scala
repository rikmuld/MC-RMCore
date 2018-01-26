package com.rikmuld.corerm.inventory.gui

import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

abstract class GuiContainerSimple(container: Container) extends GuiContainer(container) {
  protected override def drawGuiContainerBackgroundLayer(partialTick: Float, mouseX: Int, mouseY: Int) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
    mc.renderEngine.bindTexture(getTexture)
    drawGUI(mouseX, mouseY)
  }

  def drawGUI(mouseX: Int, mouseY: Int): Unit =
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)

  override def drawGuiContainerForegroundLayer(i: Int, j: Int) =
    if (hasName) drawCenteredString(fontRenderer, getName, xSize / 2, 8, 4210752)

  override def drawCenteredString(fontRender: FontRenderer, text: String, x: Int, y: Int, color: Int) =
    fontRender.drawString(text, x - fontRender.getStringWidth(text) / 2, y, color)

  def getName: String =
    ""

  def hasName: Boolean =
    false

  def getTexture: ResourceLocation
}