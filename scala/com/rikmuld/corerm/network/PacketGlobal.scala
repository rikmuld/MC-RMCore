package com.rikmuld.corerm.network

import com.rikmuld.corerm.Registry
import com.rikmuld.corerm.network.packets.PacketBasic
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, MessageContext}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

class PacketGlobal(var dataToken: Int, var data: PacketBasic) extends IMessage {
  def this() =
    this(0, null)

  override def fromBytes(buf: ByteBuf) {
    val stream: PacketBuffer = new PacketBuffer(buf)

    dataToken = stream.readInt

    data = Registry.packetRegistry.getValue(dataToken).create
    data.read(stream)
  }

  override def toBytes(buf: ByteBuf) {
    val stream: PacketBuffer = new PacketBuffer(buf)

    stream.writeInt(dataToken)
    data.write(stream)
  }

  def handlePacketServer(ctx: MessageContext): Unit =
    data.handlePacket(ctx.getServerHandler.player, ctx)

  @SideOnly(Side.CLIENT)
  def handlePacketClient(ctx: MessageContext): Unit =
    data.handlePacket(Minecraft.getMinecraft.player, ctx)
}
