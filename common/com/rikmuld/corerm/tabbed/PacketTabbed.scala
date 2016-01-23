package com.rikmuld.corerm.tabbed

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.PacketBuffer
import com.rikmuld.corerm.network.BasicPacketData
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class TabSwitch(var idLeft:Int, var idTop:Int) extends BasicPacketData {
  def this() = this(0, 0)
 
  override def handlePacket(player: EntityPlayer, ctx: MessageContext) {
    if(!player.worldObj.isRemote&&player.openContainer.isInstanceOf[ContainerTabbed])player.openContainer.asInstanceOf[ContainerTabbed].updateTab(player, idLeft, idTop)
  }
  override def getData(stream: PacketBuffer) {
    idLeft = stream.readInt
    idTop = stream.readInt
  }
  override def setData(stream: PacketBuffer) {
    stream.writeInt(idLeft)
    stream.writeInt(idTop)
  }
}