package com.rikmuld.corerm.gui.container

import com.rikmuld.corerm.gui.slots.SlotTabbed
import com.rikmuld.corerm.network.PacketSender
import com.rikmuld.corerm.network.packets.PacketTabSwitch
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container

import scala.collection.JavaConversions._

trait ContainerTabbed extends Container {
  private var tab =
    0

  def updateTab(player:EntityPlayer, index:Int){
    inventorySlots.foreach({
      case slot: SlotTabbed => slot.updateTab(player, index)
      case _ =>
    })
    
    tab = index

    if(player.world.isRemote)
      PacketSender.sendToServer(new PacketTabSwitch(index))
  }

  def getTab: Int =
    tab
}