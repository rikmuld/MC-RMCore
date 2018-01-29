package com.rikmuld.corerm.network.packets

import com.rikmuld.corerm.gui.container.ContainerTabbed
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class PacketTabSwitch(var index: Int) extends PacketBasic {
  def this() = this(0)
 
  override def handlePacket(player: EntityPlayer, ctx: MessageContext): Unit =
    if(!player.world.isRemote) player.openContainer match {
      case tabbed: ContainerTabbed => tabbed.updateTab(player, index)
      case _ =>
    }

  override def read(stream: PacketBuffer): Unit =
    index = stream.readInt

  override def write(stream: PacketBuffer): Unit =
    stream.writeInt(index)
}