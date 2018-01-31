package com.rikmuld.corerm.network

import com.rikmuld.corerm.Library.ModInfo._
import com.rikmuld.corerm.network.packets.PacketBasic
import com.rikmuld.corerm.registry.Registry
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

import scala.collection.JavaConversions._

object PacketSender {
  var network: SimpleNetworkWrapper =
    _

  def init(): Unit = {
    network = NetworkRegistry.INSTANCE.newSimpleChannel(PACKET_CHANEL)

    network.registerMessage(classOf[MessageHandler], classOf[PacketGlobal], 0, Side.SERVER)
    network.registerMessage(classOf[MessageHandler], classOf[PacketGlobal], 0, Side.CLIENT)
  }

  def sendToPlayer(data: PacketBasic, player: EntityPlayerMP): Unit =
    getWrapperForPacket(data.getClass).foreach(_.sendToPlayer(data, player))

  def sendToServer(data: PacketBasic): Unit =
    getWrapperForPacket(data.getClass).foreach(_.sendToServer(data))

  def sendToClient(data: PacketBasic): Unit =
    getWrapperForPacket(data.getClass).foreach(_.sendToClient(data))

  def getWrapperForPacket[A <: PacketBasic](packet: Class[A]): Option[PacketWrapper] =
    Registry.packetRegistry.getValues.find(_.getPacket == packet)
}