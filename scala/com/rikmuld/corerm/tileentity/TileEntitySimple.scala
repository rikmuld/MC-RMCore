package com.rikmuld.corerm.tileentity

import com.rikmuld.corerm.network.PacketSender
import com.rikmuld.corerm.network.packets.PacketTileData
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TileEntitySimple extends TileEntity {
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
      PacketSender.sendToServer(getDataPacket(id, data))
    else if (toClient && !world.isRemote)
      PacketSender.sendToClient(getDataPacket(id, data))

  def sendTileDataTo(id: Int, player: EntityPlayer, data: Int*): Unit =
    if(!world.isRemote)
      PacketSender.sendToPlayer(getDataPacket(id, data), player.asInstanceOf[EntityPlayerMP])

  private def getDataPacket(id: Int, data: Seq[Int]): PacketTileData =
    new PacketTileData(id, pos.getX, pos.getY, pos.getZ, data)

  override def getUpdateTag: NBTTagCompound =
    this.writeToNBT(super.getUpdateTag)

  def init(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos): Unit =
    Unit
}