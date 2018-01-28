package com.rikmuld.corerm.gui.gui

import com.rikmuld.corerm.Lib._
import com.rikmuld.corerm.gui.container.ContainerTabbed
import com.rikmuld.corerm.gui.gui.buttons.ButtonTabbed
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

  def getTab(tab: Int): Tab =
    tabs(tab)

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
      case _ =>
    }
  }

  def drawPage(tab: Int, mouseX: Int, mouseY: Int)

  def isEnabled(tab: Int): Boolean =
    tabs(tab).isEnabled

  def enable(tab: Int): Unit =
    tabs(tab).enable()

  def disable(tab: Int): Unit =
    tabs(tab).disable()

  def setEnabled(tab: Int, enable: Boolean): Unit =
    tabs(tab).setEnabled(enable)

  override def drawGUI(mouseX: Int, mouseY: Int) {
    super.drawGUI(mouseX, mouseY)

    drawPage(tab, mouseX, mouseY)

    mc.renderEngine.bindTexture(Tab.TEXT_UTILS_TAB)

    tabs.foreach(_.drawBackground())
    tabs.foreach(tab => {
      bindTexture()
      tab.drawForeground(fontRenderer, itemRender)
    })
  }
}

object Tab {
  final val TEXT_UTILS_TAB = new ResourceLocation(TextureInfo.GUI_TAB_UTILS)
}

class Tab(name: String, offset: Int, parent: GuiTabbed, id: Int) {
  private var active: Boolean =
    id == 0

  private var stack: Option[ItemStack] =
    None

  private var textureInfo: Option[(Int, Int)] =
    None

  private var enabled: Boolean =
    true

  val left: Int =
    parent.getGuiLeft + id * 28 + offset + 4

  val top: Int =
    parent.getGuiTop - 20

  def this(name: String, offset: Int, parent: GuiTabbed, id: Int, item: ItemStack) {
    this(name, offset, parent, id)
    this.stack = Some(item)
  }

  def this(name: String, offset: Int, parent: GuiTabbed, id: Int, textureX: Int, textureY: Int) {
    this(name, offset, parent, id)
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

  def setEnabled(enable: Boolean): Unit =
    enabled = enable

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

    if (active) parent.drawTexturedModalRect(left, top, 26, 43, 24, 23)
    else parent.drawTexturedModalRect(left, top, 26, 20, 24, 23)

    if(!enabled)
      GL11.glColor3f(1, 1, 1)
  }

  def drawForeground(fontRenderer: FontRenderer, itemRenderer: RenderItem): Unit = {
    stack.foreach(item => drawBackgroundItem(item, itemRenderer))
    textureInfo.foreach(info => drawBackgroundTexture(info._1, info._2))
  }

  def drawBackgroundItem(item: ItemStack, renderer: RenderItem): Unit =
    renderer.renderItemAndEffectIntoGUI(item, left + 4, top + 3)

  def drawBackgroundTexture(v: Int, u: Int): Unit =
    parent.drawTexturedModalRect(left + 4, top + 3, v, u, 16, 16)
}