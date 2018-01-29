package com.rikmuld.corerm.network.packets

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

trait PacketBasic {
  def write(stream: PacketBuffer): Unit

  def read(stream: PacketBuffer): Unit

  def handlePacket(player: EntityPlayer, ctx: MessageContext): Unit
}