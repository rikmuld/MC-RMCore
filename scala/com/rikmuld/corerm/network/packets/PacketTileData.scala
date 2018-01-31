package com.rikmuld.corerm.network.packets

import com.rikmuld.corerm.tileentity.TileEntitySimple
import com.rikmuld.corerm.utils.MathUtils._
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class PacketTileData(var id: Int, var x: Int, var y: Int, var z: Int, tileData: Seq[Int]) extends PacketBasic {
  var length: Int =
    tileData.length * 4

  var data: Array[Byte] = {
    for (i <- tileData.indices)
      yield intToBytes(tileData(i))
  }.flatten.toArray

  def this() =
    this(0, 0, 0, 0, Seq())

  def write(stream: PacketBuffer) {
    stream.writeInt(id)
    stream.writeInt(x)
    stream.writeInt(y)
    stream.writeInt(z)
    stream.writeInt(length)
    stream.writeBytes(data)
  }

  def read(stream: PacketBuffer) {
    id = stream.readInt
    x = stream.readInt
    y = stream.readInt
    z = stream.readInt
    length = stream.readInt
    data = new Array[Byte](length)
    stream.readBytes(data)
  }

  def handlePacket(player: EntityPlayer, ctx: MessageContext) {
    val pos = new BlockPos(x, y, z)
    val intData =
      for (i <- 0 until length / 4)
        yield bytesToInt (
          for (j <- 0 until 4)
            yield data(j + (i * 4))
        )

    Option(player).flatMap(player => Option(player.world)).flatMap(world => Option(world.getTileEntity(pos))).foreach {
      case tile: TileEntitySimple => tile.setTileData(id, intData)
      case _ =>
    }
  }
}