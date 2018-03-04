package com.rikmuld.corerm.tileentity

import com.rikmuld.corerm.network.PacketSender
import com.rikmuld.corerm.network.packets.PacketBounds
import com.rikmuld.corerm.objs.blocks.BlockSimple
import com.rikmuld.corerm.objs.blocks.bounds.Bounds
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}

class TileEntityBounds extends TileEntitySimple {
  private var bounds: Bounds =
    _

  private var baseX: Int =
    _

  private var baseY: Int =
    _

  private var baseZ: Int =
    _

  override def readFromNBT(tag: NBTTagCompound) {
    super.readFromNBT(tag)

    baseX = tag.getInteger("baseX")
    baseY = tag.getInteger("baseY")
    baseZ = tag.getInteger("baseZ")

    if(Bounds.canRead(tag))
      bounds = Bounds.readFromNBT(tag)
  }

  override def writeToNBT(tag: NBTTagCompound):NBTTagCompound = {
    tag.setInteger("baseX", baseX)
    tag.setInteger("baseY", baseY)
    tag.setInteger("baseZ", baseZ)

    Option(bounds).foreach(_.writeToNBT(tag))

    super.writeToNBT(tag)
  }

  override def setTileData(id: Int, data: Seq[Int]): Unit =
    if (id == 0) {
      baseX = data.head
      baseY = data(1)
      baseZ = data(2)
    }


  def setBaseCoords(x: Int, y: Int, z: Int) {
    baseX = x
    baseY = y
    baseZ = z

    sendTileData(0, true, x, y, z)
  }

  def setBounds(bounds: Bounds) {
    this.bounds = bounds

    if (!world.isRemote)
      PacketSender.sendToClient(new PacketBounds(Some(bounds), getPos.getX, getPos.getY, getPos.getZ))
  }

  def getBounds: AxisAlignedBB =
    Option(bounds).fold(BlockSimple.BOUNDS_EMPTY)(_.bounds)

  def getCollisionBounds: AxisAlignedBB =
    Option(bounds).fold(BlockSimple.BOUNDS_EMPTY)(_.collisionBounds)

  def getBasePos: BlockPos =
    new BlockPos(baseX, baseY, baseZ)
}