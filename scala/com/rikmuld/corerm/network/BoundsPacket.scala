package com.rikmuld.corerm.network

import com.rikmuld.corerm.tileentity.TileEntityBounds
import com.rikmuld.corerm.utils.Bounds
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class BoundsData(var bounds: Bounds, var x: Int, var y: Int, var z: Int) extends BasicPacketData {
  var xMin = if (bounds != null) bounds.xMin else 0
  var yMin = if (bounds != null) bounds.yMin else 0
  var zMin = if (bounds != null) bounds.zMin else 0
  var xMax = if (bounds != null) bounds.xMax else 0
  var yMax = if (bounds != null) bounds.yMax else 0
  var zMax = if (bounds != null) bounds.zMax else 0

  def this() = this(null, 0, 0, 0)
  override def getData(stream: PacketBuffer) {
    x = stream.readInt
    y = stream.readInt
    z = stream.readInt
    xMin = stream.readFloat
    yMin = stream.readFloat
    zMin = stream.readFloat
    xMax = stream.readFloat
    yMax = stream.readFloat
    zMax = stream.readFloat
  }
  override def setData(stream: PacketBuffer) {
    stream.writeInt(x)
    stream.writeInt(y)
    stream.writeInt(z)
    stream.writeFloat(xMin)
    stream.writeFloat(yMin)
    stream.writeFloat(zMin)
    stream.writeFloat(xMax)
    stream.writeFloat(yMax)
    stream.writeFloat(zMax)
  }
  override def handlePacket(player: EntityPlayer, ctx: MessageContext) {
    if (player.world.getTileEntity(new BlockPos(x, y, z)) != null) {
      player.world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileEntityBounds].bounds = new Bounds(xMin, yMin, zMin, xMax, yMax, zMax)
    }
  }
}