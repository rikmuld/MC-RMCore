package com.rikmuld.corerm.bounds

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import com.rikmuld.corerm.CoreUtils._
import com.rikmuld.corerm.misc.WorldBlock._
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB

object Bounds {
  def readBoundsToNBT(tag: NBTTagCompound): Bounds = {
    new Bounds(tag.getFloat("xMin"), tag.getFloat("yMin"), tag.getFloat("zMin"), tag.getFloat("xMax"), tag.getFloat("yMax"), tag.getFloat("zMax"))
  }
}

class Bounds(var xMin: Float, var yMin: Float, var zMin: Float, var xMax: Float, var yMax: Float, var zMax: Float) {
  def getBlockBounds(block: Block):AxisAlignedBB = 
    new AxisAlignedBB(xMin, yMin, zMin, xMax, yMax, zMax)
  def getBlockCollision(block: Block):AxisAlignedBB =
    new AxisAlignedBB(Math.max(xMin, 0), Math.max(yMin, 0), Math.max(zMin, 0), Math.min(xMax, 1), Math.min(yMax, 1), Math.min(zMax, 1))
   
  def writeBoundsToNBT(tag: NBTTagCompound) {
    tag.setFloat("xMin", xMin)
    tag.setFloat("yMin", yMin)
    tag.setFloat("zMin", zMin)
    tag.setFloat("xMax", xMax)
    tag.setFloat("yMax", yMax)
    tag.setFloat("zMax", zMax)
  }
  override def toString() = super.toString + ":" + xMin + ":" + yMin + ":" + zMin + ":" + xMax + ":" + yMax + ":" + zMax;
}

object BoundsStructure {
  def regsisterStructure(xCoords: Array[Int], yCoords: Array[Int], zCoords: Array[Int], rotation: Boolean): Array[BoundsStructure] = {
    if (!rotation) Array(new BoundsStructure(Array(xCoords, yCoords, zCoords)))
    else {
      val structure = Array.ofDim[BoundsStructure](4)
      structure(0) = new BoundsStructure(Array(xCoords, yCoords, zCoords))
      structure(1) = new BoundsStructure(Array(zCoords.inverse, yCoords, xCoords.inverse))
      structure(2) = new BoundsStructure(Array(xCoords.inverse, yCoords, zCoords.inverse))
      structure(3) = new BoundsStructure(Array(zCoords, yCoords, xCoords))
      structure
    }
  }
}

class BoundsStructure(var blocks: Array[Array[Int]]) {
  def canBePlaced(world: World, tracker: BoundsTracker): Boolean = {
    for (i <- 0 until blocks(0).length) {
      val bd = getBD(world, tracker, i)
      if (bd.block != Blocks.AIR && !bd.isReplaceable) return false
    }
    if (hadSolidUnderGround(world, tracker)) true else false
  }
  def createStructure(world: World, tracker: BoundsTracker, boundsBlockState:IBlockState) {
    if(boundsBlockState.getBlock.isInstanceOf[IBoundsBlock]){
      for (i <- 0 until blocks(0).length) {
        val bd = getBD(world, tracker, i)
        bd.setState(boundsBlockState, 2)
        bd.tile.asInstanceOf[TileBounds].setBounds(tracker.getBoundsOnRelativePoistion(blocks(0)(i), blocks(1)(i), blocks(2)(i)))
        bd.tile.asInstanceOf[TileBounds].setBaseCoords(tracker.baseX, tracker.baseY, tracker.baseZ)
      }
    }
  }
  def getBD(world:World, tracker:BoundsTracker, index:Int):BlockData = (world, new BlockPos(tracker.baseX + blocks(0)(index), tracker.baseY + blocks(1)(index), tracker.baseZ + blocks(2)(index)))
  def destroyStructure(world: World, tracker: BoundsTracker) = for (i <- 0 until blocks(0).length) getBD(world, tracker, i).toAir
  def hadSolidUnderGround(world: World, tracker: BoundsTracker): Boolean = {
    (0 until blocks(0).length).find(i => !world.isSideSolid(new BlockPos(tracker.baseX + blocks(0)(i), tracker.baseY - 1, tracker.baseZ + blocks(2)(i)), EnumFacing.UP)).map(_ => false).getOrElse(true)
  }
}

class BoundsTracker(var baseX: Int, var baseY: Int, var baseZ: Int, var bounds: Bounds) {
  def getBoundsOnRelativePoistion(xDiv: Int, yDiv: Int, zDiv: Int): Bounds = new Bounds(bounds.xMin - xDiv, bounds.yMin - yDiv, bounds.zMin - zDiv, bounds.xMax - xDiv, bounds.yMax - yDiv, bounds.zMax - zDiv)
}