package com.rikmuld.corerm.network

import com.rikmuld.corerm.RMMod._
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.mutable.ArrayBuffer

object PacketDataManager {
  var dataMap: ArrayBuffer[Class[BasicPacketData]] = new ArrayBuffer[Class[BasicPacketData]]

  def registerPacketData(data: Class[BasicPacketData]) = if (!dataMap.contains(data)) dataMap.+=(data)
  def getTokenForData(data: BasicPacketData): Int = {
    for (x <- 0 until dataMap.size if (dataMap.apply(x).equals(data.getClass.asInstanceOf[Class[BasicPacketData]]))) return x
    -1
  }
}

object PacketSender {
  def to(data: BasicPacketData, player: EntityPlayer) = network.sendTo(new PacketGlobal(data), player.asInstanceOf[EntityPlayerMP])
  def toServer(data: BasicPacketData) = network.sendToServer(new PacketGlobal(data))
  def toClient(data: BasicPacketData) = network.sendToAll(new PacketGlobal(data))
}

trait BasicPacketData {
  def setData(stream: PacketBuffer)
  def getData(stream: PacketBuffer)
  def handlePacket(player: EntityPlayer, ctx: MessageContext)
}

class PacketGlobal(var data: BasicPacketData) extends IMessage {
  var dataToken: Int = if (data == null) 0 else PacketDataManager.getTokenForData(data)

  def this() = this(null)
  override def fromBytes(buf: ByteBuf) {
    val stream: PacketBuffer = new PacketBuffer(buf)
    dataToken = stream.readInt
    data = PacketDataManager.dataMap.apply(dataToken).newInstance
    data.getData(stream)
  }
  override def toBytes(buf: ByteBuf) {
    val stream: PacketBuffer = new PacketBuffer(buf)
    stream.writeInt(dataToken)
    data.setData(stream)
  }
  def handlePacketServer(ctx: MessageContext) = data.handlePacket(ctx.getServerHandler.playerEntity, ctx)
  @SideOnly(Side.CLIENT)
  def handlePacketClient(ctx: MessageContext) = data.handlePacket(Minecraft.getMinecraft.player, ctx)
}

class Handler extends IMessageHandler[PacketGlobal, IMessage] {
  override def onMessage(message: PacketGlobal, ctx: MessageContext): IMessage = {    
    if (ctx.side.equals(Side.SERVER)) message.handlePacketServer(ctx)
    else message.handlePacketClient(ctx)
    null
  }
}