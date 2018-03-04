package com.rikmuld.corerm.utils

import net.minecraft.util.math.AxisAlignedBB

/**
  Functions are defined as:

  flipZ(a) == CWZtoX(CWXtoZ(a))
  flipX(a) == CWXtoZ(CWZtoX(a))
  CWXtoZ(flipZ(a)) == flipX(CWXtoZ(a))
  CWZtoX(flipX(a)) == flipZ(CWZtoX(a))

  They can be used to rotate the bounding box of blocks
  */
object AxisUtils {
  def flipX(axis: AxisAlignedBB): AxisAlignedBB =
    new AxisAlignedBB(
      1 - axis.maxX,
      axis.minY,
      axis.minZ,
      1 - axis.minX,
      axis.maxY,
      axis.maxZ
    )

  def flipZ(axis: AxisAlignedBB): AxisAlignedBB =
    new AxisAlignedBB(
      axis.minX,
      axis.minY,
      1 - axis.maxZ,
      axis.maxX,
      axis.maxY,
      1 - axis.minZ
    )

  def CWXtoZ(axis: AxisAlignedBB): AxisAlignedBB =
    new AxisAlignedBB(
      axis.minZ,
      axis.minY,
      axis.minX,
      axis.maxZ,
      axis.maxY,
      axis.maxX
    )

  def CWZtoX(axis: AxisAlignedBB): AxisAlignedBB =
    new AxisAlignedBB(
      axis.minZ,
      axis.minY,
      1 - axis.maxX,
      axis.maxZ,
      axis.maxY,
      1 - axis.minX
    )
}
