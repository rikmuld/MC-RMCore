package com.rikmuld.corerm.objs.blocks.bounds

import com.rikmuld.corerm.tileentity.TileEntityBounds
import com.rikmuld.corerm.utils.AxisUtils._
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.World

object BoundsStructure {
  // make sure bounds extends in the z-direction (i.e. minZ >= 0)
  def createWithRotation(bounds: AxisAlignedBB): Seq[BoundsStructure] =
    Vector(
      new BoundsStructure(bounds),
      new BoundsStructure(CWXtoZ(flipZ(bounds))),
      new BoundsStructure(flipZ(bounds)),
      new BoundsStructure(CWXtoZ(bounds))
    )
}

class BoundsStructure(bounds: AxisAlignedBB) {
  private val blocks:Seq[(Int, Int, Int)] =
    for(
      x <- Math.floor(bounds.minX).toInt until Math.ceil(bounds.maxX).toInt;
      y <- Math.floor(bounds.minY).toInt until Math.ceil(bounds.maxY).toInt;
      z <- Math.floor(bounds.minZ).toInt until Math.ceil(bounds.maxZ).toInt
    ) yield (x, y, z)

  private val coords: (Seq[Int], Seq[Int], Seq[Int]) =
    blocks.unzip3

  val groundIds: Seq[Int] =
    coords._2.zipWithIndex.filter(_._1 == 0).map(_._2)

  val allIds: Seq[Int] =
    coords._1.indices

  val baseBounds: Bounds =
    Bounds.fromAxis(bounds)

  def canBePlaced(world: World, pos: BlockPos): Boolean =
    isReplaceable(world, pos) && hadSolidUnderGround(world, pos)

  def hadSolidUnderGround(world: World, pos: BlockPos): Boolean =
    groundIds.forall(i =>
      world.isSideSolid(new BlockPos(
        pos.getX + coords._1(i),
        pos.getY - 1,
        pos.getZ + coords._3(i)
      ), EnumFacing.UP)
    )

  def isReplaceable(world: World, pos: BlockPos): Boolean =
    allIds.forall(i => {
      val relPos = getPos(pos, i)
      world.getBlockState(relPos).getBlock.isReplaceable(world, relPos)
    })

  def destroyStructure(world: World, pos: BlockPos): Unit =
    for (i <- allIds)
      world.setBlockToAir(getPos(pos, i))

  def createStructure(fillWith: IBlockState, world: World, pos: BlockPos): Unit =
    if(!exists(fillWith, world, pos) && fillWith.getBlock.isInstanceOf[BlockBounds])
      for (i <- allIds) {
        val relPos = getPos(pos, i)

        if (!relPos.equals(pos)) {
          world.setBlockState(relPos, fillWith)
          world.getTileEntity(relPos) match {
            case tile: TileEntityBounds =>
              tile.setBounds(getBoundsFor(bounds, i))
              tile.setBaseCoords(pos.getX, pos.getY, pos.getZ)
          }
        }
      }

  def exists(fillWith: IBlockState, world: World, pos: BlockPos): Boolean =
    allIds.exists(i =>
      world.getBlockState(getPos(pos, i)).getBlock == fillWith.getBlock
    )

  def getBoundsFor(base: AxisAlignedBB, i: Int): Bounds =
    new Bounds(
      base.minX - coords._1(i),
      base.minY - coords._2(i),
      base.minZ - coords._3(i),
      base.maxX - coords._1(i),
      base.maxY - coords._2(i),
      base.maxZ - coords._3(i)
    )

  def getPos(pos: BlockPos, i:Int): BlockPos =
    new BlockPos(
      pos.getX + coords._1(i),
      pos.getY + coords._2(i),
      pos.getZ + coords._3(i)
    )

  def getBounds: Bounds =
    baseBounds
}