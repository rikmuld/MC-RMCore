package com.rikmuld.corerm.bounds

import com.rikmuld.corerm.objs.RMTile
import net.minecraft.nbt.NBTTagCompound
import com.rikmuld.corerm.network.PacketSender
import com.rikmuld.corerm.misc.WorldBlock._
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos

class TileBounds extends RMTile with ITickable {
  var bounds: Bounds = _
  var baseX: Int = _
  var baseY: Int = _
  var baseZ: Int = _
  private var updateNeed: Boolean = _

  override def readFromNBT(tag: NBTTagCompound) {
    super.readFromNBT(tag)
    baseX = tag.getInteger("baseX")
    baseY = tag.getInteger("baseY")
    baseZ = tag.getInteger("baseZ")
    if (tag.hasKey("xMin")) setBounds(Bounds.readBoundsToNBT(tag))
  }
  def setBaseCoords(x: Int, y: Int, z: Int) {
    baseX = x
    baseY = y
    baseZ = z
    sendTileData(0, true, x, y, z)
  }
  def setBounds(bounds: Bounds) {
    this.bounds = bounds
    updateNeed = true
  }
  def basePos = new BlockPos(baseX, baseY, baseZ)
  override def setTileData(id: Int, data: Array[Int]) {
    if (id == 0) {
      baseX = data(0)
      baseY = data(1)
      baseZ = data(2)
    }
  }
  override def update() {
    if (!worldObj.isRemote && updateNeed) {
      PacketSender.toClient(new BoundsData(bounds, bd.x, bd.y, bd.z))
      updateNeed = false
    }
  }
  override def writeToNBT(tag: NBTTagCompound):NBTTagCompound = {
    tag.setInteger("baseX", baseX)
    tag.setInteger("baseY", baseY)
    tag.setInteger("baseZ", baseZ)
    if (bounds != null) bounds.writeBoundsToNBT(tag)
    super.writeToNBT(tag)
  }
}