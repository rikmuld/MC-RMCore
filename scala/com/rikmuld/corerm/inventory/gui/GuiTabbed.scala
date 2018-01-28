package com.rikmuld.corerm.inventory.gui

import com.rikmuld.corerm.Lib._
import com.rikmuld.corerm.inventory.container.ContainerTabbed
import com.rikmuld.corerm.inventory.gui.buttons.ButtonTabbed
import com.rikmuld.corerm.utils.CoreUtils
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.RenderItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

import scala.collection.JavaConversions._

abstract class GuiTabbed(player:EntityPlayer, container: ContainerTabbed) extends GuiContainerSimple(container) {
  private var tabs: Seq[Tab] =
    _

  private var tab: Int =
    0

  override def initGui() {
    super.initGui()

    tabs = getTabs
    setTab(0)
  }

  def getTabs: Seq[Tab]

  def getActive: Int =
    tab

  override def renderHoveredToolTip(mouseX : Int, mouseY : Int): Unit = {
    tabs.foreach(_.handleHover(mouseX, mouseY))
    super.renderHoveredToolTip(mouseX, mouseY)
  }

  override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
    tabs.foreach(_.handleClick(mouseX, mouseY, mouseButton))
    super.mouseClicked(mouseX, mouseY, mouseButton)
  }

  def setTab(index: Int): Unit = {
    tab = index

    container.updateTab(player, tab)

    tabs.foreach(_.setTab(tab))
    buttonList.foreach {
      case button: ButtonTabbed => button.updateTab(tab)
      case _ => _
    }
  }

  def drawPage(tab: Int)

  override def drawGUI(mouseX: Int, mouseY: Int) {
    drawPage(tab)

    mc.renderEngine.bindTexture(Tab.TEXT_UTILS_TAB)
    tabs.foreach(_.drawBackground())

    mc.renderEngine.bindTexture(getTexture)
    tabs.foreach(_.drawForeground(fontRenderer, itemRender))
  }
}

object Tab {
  final val TEXT_UTILS_TAB = new ResourceLocation(TextureInfo.GUI_TAB_UTILS)
}

class Tab(name: String, parent: GuiTabbed, id: Int) {
  private var active: Boolean =
    id == 0

  private var stack: Option[ItemStack] =
    None

  private var textureInfo: Option[(Int, Int)] =
    None

  private var enabled: Boolean =
    true

  val left: Int =
    parent.getGuiLeft + 4 + id * 28

  val top: Int =
    parent.getGuiTop - 20

  def this(name: String, parent: GuiTabbed, id: Int, item: ItemStack) {
    this(name, parent, id)
    this.stack = Some(item)
  }

  def this(name: String, parent: GuiTabbed, id: Int, textureX: Int, textureY: Int) {
    this(name, parent, id)
    this.textureInfo = Some((textureX, textureY))
  }

  def setTab(index: Int): Unit =
    active = id == index

  def isEnabled: Boolean =
    enabled

  def isActive: Boolean =
    active

  def enable(): Unit =
    enabled = true

  def disable(): Unit =
    enabled = false

  def handleHover(mouseX: Int, mouseY: Int): Unit =
    if(isHover(mouseX, mouseY))
      parent.drawHoveringText(name, mouseX, mouseY)

  def handleClick(mouseX: Int, mouseY: Int, button: Int): Unit =
    if (button == 0 && isHover(mouseX, mouseY) && enabled)
      parent.setTab(id)

  def isHover(mouseX: Int, mouseY: Int): Boolean =
    CoreUtils.isInBox(left, top, 24, 20)(mouseX, mouseY)

  def drawBackground(): Unit = {
    if(!enabled)
      GL11.glColor3f(.5f, .5f, .5f)

    if(active) parent.drawTexturedModalRect(left, top, 0, 46, 26, 26)
    else parent.drawTexturedModalRect(left, top, 0, 20, 26, 26)

    if(!enabled)
      GL11.glColor3f(1, 1, 1)
  }

  def drawForeground(fontRenderer: FontRenderer, itemRenderer: RenderItem): Unit = {
    stack.foreach(item => drawBackgroundItem(item, itemRenderer))
    textureInfo.foreach(info => drawBackgroundTexture(info._1, info._2))
  }

  def drawBackgroundItem(item: ItemStack, renderer: RenderItem): Unit =
    renderer.renderItemAndEffectIntoGUI(item, left + 5, top + 5)

  def drawBackgroundTexture(v: Int, u: Int): Unit =
    parent.drawTexturedModalRect(left + 5, top + 5, v, u, 16, 16)
}