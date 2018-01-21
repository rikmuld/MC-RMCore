package com.rikmuld.corerm.tabbed

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import scala.collection.JavaConversions._
import com.rikmuld.corerm.network.PacketSender

trait ContainerTabbed extends Container {
  var leftActive = 0
  var topActive = 0
  
  def updateTab(player:EntityPlayer, idLeft:Int, idTop:Int){
    inventorySlots.foreach(slot => {if(slot.isInstanceOf[SlotWithTabs])slot.asInstanceOf[SlotWithTabs].updateTab(player, idLeft, idTop)})
    
    leftActive = idLeft
    topActive = idTop
    
    if(player.world.isRemote)PacketSender.toServer(new TabSwitch(idLeft, idTop))
  }
  def getCurrentTab = List(leftActive, topActive)
  def getCurrentTabTop = topActive
  def getCurrentTabLeft = leftActive
}