package com.rikmuld.corerm.features.tabbed

import com.rikmuld.corerm.network.PacketSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container

import scala.collection.JavaConversions._

trait ContainerTabbed extends Container {
  var leftActive = 0

  var topActive = 0
  
  def updateTab(player:EntityPlayer, idLeft:Int, idTop:Int){
    inventorySlots.foreach(_ match {
      case slot: SlotWithTabs => slot.updateTab(player, idLeft, idTop)
      case slot =>
    })
    
    leftActive = idLeft
    topActive = idTop
    
    if(player.world.isRemote)
      PacketSender.toServer(new TabSwitch(idLeft, idTop))
  }

  def getCurrentTab =
    List(leftActive, topActive)

  def getCurrentTabTop =
    topActive

  def getCurrentTabLeft =
    leftActive
}