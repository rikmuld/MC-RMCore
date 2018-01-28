package com.rikmuld.corerm.inventory.container

import com.rikmuld.corerm.inventory.slots.SlotTabbed
import com.rikmuld.corerm.network.{PacketSender, PacketTabSwitch}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container

import scala.collection.JavaConversions._

trait ContainerTabbed extends Container {
  private var tab =
    0

  def updateTab(player:EntityPlayer, index:Int){
    inventorySlots.foreach({
      case slot: SlotTabbed => slot.updateTab(player, index)
      case _ => _
    })
    
    tab = index

    if(player.world.isRemote)
      PacketSender.toServer(new PacketTabSwitch(index))
  }

  def getTab: Int =
    tab
}