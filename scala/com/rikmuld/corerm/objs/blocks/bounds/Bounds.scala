package com.rikmuld.corerm.objs.blocks.bounds

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.AxisAlignedBB

object Bounds {
  def readFromNBT(tag: NBTTagCompound): Bounds =
    Bounds(
      tag.getDouble("xMin"),
      tag.getDouble("yMin"),
      tag.getDouble("zMin"),
      tag.getDouble("xMax"),
      tag.getDouble("yMax"),
      tag.getDouble("zMax")
    )

  def canRead(tag: NBTTagCompound): Boolean =
    tag.hasKey("xMin")

  def fromAxis(axis: AxisAlignedBB): Bounds =
    Bounds(axis.minX, axis.minY, axis.minZ, axis.maxX, axis.maxY, axis.maxZ)
}

case class Bounds(xMin: Double, yMin: Double, zMin: Double, xMax: Double, yMax: Double, zMax: Double) {
  val bounds: AxisAlignedBB =
    new AxisAlignedBB(xMin, yMin, zMin, xMax, yMax, zMax)

  val collisionBounds :AxisAlignedBB =
    new AxisAlignedBB(
      Math.max(xMin, 0),
      Math.max(yMin, 0),
      Math.max(zMin, 0),
      Math.min(xMax, 1),
      Math.min(yMax, 1),
      Math.min(zMax, 1)
    )

  def writeToNBT(tag: NBTTagCompound) {
    tag.setDouble("xMin", xMin)
    tag.setDouble("yMin", yMin)
    tag.setDouble("zMin", zMin)
    tag.setDouble("xMax", xMax)
    tag.setDouble("yMax", yMax)
    tag.setDouble("zMax", zMax)
  }
}