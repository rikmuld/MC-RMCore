package com.rikmuld.corerm.network.packets

import com.rikmuld.corerm.objs.blocks.bounds.Bounds
import com.rikmuld.corerm.tileentity.TileEntityBounds
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

//TODO should not be necessary
class PacketBounds(bounds: Option[Bounds], var x: Int, var y: Int, var z: Int) extends PacketBasic {
  var xMin,
      yMin,
      zMin,
      xMax,
      yMax,
      zMax: Double =
    0

  bounds.foreach(bounds => {
    xMin = bounds.xMin
    yMin = bounds.yMin
    zMin = bounds.zMin
    xMax = bounds.xMax
    yMax = bounds.yMax
    zMax = bounds.zMax
  })


  def this() =
    this(None, 0, 0, 0)

  override def read(stream: PacketBuffer) {
    x = stream.readInt
    y = stream.readInt
    z = stream.readInt

    xMin = stream.readDouble
    yMin = stream.readDouble
    zMin = stream.readDouble
    xMax = stream.readDouble
    yMax = stream.readDouble
    zMax = stream.readDouble
  }

  override def write(stream: PacketBuffer) {
    stream.writeInt(x)
    stream.writeInt(y)
    stream.writeInt(z)

    stream.writeDouble(xMin)
    stream.writeDouble(yMin)
    stream.writeDouble(zMin)
    stream.writeDouble(xMax)
    stream.writeDouble(yMax)
    stream.writeDouble(zMax)
  }

  override def handlePacket(player: EntityPlayer, ctx: MessageContext): Unit =
    player.world.getTileEntity(new BlockPos(x, y, z)) match {
      case tile: TileEntityBounds =>
        tile.setBounds(new Bounds(xMin, yMin, zMin, xMax, yMax, zMax))
      case _ =>
    }
}