package com.rikmuld.corerm.network

import com.rikmuld.corerm.Registry
import com.rikmuld.corerm.network.PacketSender.network
import com.rikmuld.corerm.network.packets.PacketBasic
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry

object PacketWrapper {
  def create[A <: PacketBasic](packet: Class[A], name: String): PacketWrapper =
    new PacketWrapper(packet.asInstanceOf[Class[PacketBasic]]).setRegistryName(name)

  def create[A <: PacketBasic](packet: Class[A], name: ResourceLocation): PacketWrapper =
    new PacketWrapper(packet.asInstanceOf[Class[PacketBasic]]).setRegistryName(name)
}

class PacketWrapper(packetClass: Class[PacketBasic]) extends IForgeRegistryEntry.Impl[PacketWrapper] {
  def create: PacketBasic =
    packetClass.newInstance

  def getID: Int =
    Registry.packetRegistry.getID(this)

  def getPacket: Class[PacketBasic] =
    packetClass

  def sendToClient(data: PacketBasic): Unit =
    network.sendToAll(new PacketGlobal(getID, data))

  def sendToPlayer(data: PacketBasic, player: EntityPlayerMP): Unit =
    network.sendTo(new PacketGlobal(getID, data), player)

  def sendToServer(data: PacketBasic): Unit =
    network.sendToServer(new PacketGlobal(getID, data))
}