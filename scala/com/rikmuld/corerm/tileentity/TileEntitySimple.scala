package com.rikmuld.corerm.tileentity

import com.rikmuld.corerm.network.PacketSender
import com.rikmuld.corerm.network.packets.PacketTileData
import com.rikmuld.corerm.utils.WorldBlock.BlockData
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity

class TileEntitySimple extends TileEntity {
  def bd: BlockData =
    (world, pos)

  override def readFromNBT(tag: NBTTagCompound): Unit =
    super.readFromNBT(tag)

  override def writeToNBT(tag: NBTTagCompound): NBTTagCompound =
    super.writeToNBT(tag)

  override def getUpdatePacket: SPacketUpdateTileEntity =
    new SPacketUpdateTileEntity(pos, 1, getUpdateTag)

  override def onDataPacket(net: NetworkManager, packet: SPacketUpdateTileEntity): Unit =
    readFromNBT(packet.getNbtCompound)

  def setTileData(id: Int, data: Seq[Int]): Unit =
    Unit

  def sendTileData(id: Int, toClient: Boolean, data: Int*): Unit =
    if (!toClient && world.isRemote)
      PacketSender.sendToServer(new PacketTileData(id, pos.getX, pos.getY, pos.getZ, data))
    else if (toClient && !world.isRemote)
      PacketSender.sendToClient(new PacketTileData(id, pos.getX, pos.getY, pos.getZ, data))

  override def getUpdateTag: NBTTagCompound =
    this.writeToNBT(super.getUpdateTag)
}