package com.rikmuld.corerm.utils

import com.rikmuld.corerm.utils.WorldBlock._
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

import scala.collection.mutable

object Rotation {
  private type RotData =
    (Int, Int, Int)

  private type Rotation =
    (IMLazyBlockData) => Boolean
  
  val blocksRot: mutable.Map[Block, Either[Rotation, RotData]] = mutable.Map(
      Blocks.FURNACE -> Right((6, 2, 1)),
      Blocks.LIT_FURNACE -> Right((6, 2, 1)),
      Blocks.OAK_STAIRS -> Right((8, 0, 1)),
      Blocks.STONE_STAIRS -> Right((8, 0, 1)),
      Blocks.BRICK_STAIRS -> Right((8, 0, 1)),
      Blocks.STONE_BRICK_STAIRS -> Right((8, 0, 1)),
      Blocks.NETHER_BRICK_STAIRS -> Right((8, 0, 1)),
      Blocks.SANDSTONE_STAIRS -> Right((8, 0, 1)),
      Blocks.SPRUCE_STAIRS -> Right((8, 0, 1)),
      Blocks.BIRCH_STAIRS -> Right((8, 0, 1)),
      Blocks.JUNGLE_STAIRS -> Right((8, 0, 1)),
      Blocks.QUARTZ_STAIRS -> Right((8, 0, 1)),
      Blocks.ACACIA_STAIRS -> Right((8, 0, 1)),
      Blocks.DARK_OAK_STAIRS -> Right((8, 0, 1)),
      Blocks.RED_SANDSTONE_STAIRS -> Right((8, 0, 1)),
      Blocks.CHEST -> Right((6, 2, 1)),
      Blocks.TRAPPED_CHEST -> Right((6, 2, 1)),
      Blocks.ENDER_CHEST -> Right((6, 2, 1)),
      Blocks.DISPENSER -> Right((6, 0, 1)),
      Blocks.DROPPER -> Right((6, 0, 1)),
      Blocks.PUMPKIN -> Right((4, 0, 1)),
      Blocks.LIT_PUMPKIN -> Right((4, 0, 1)),
      Blocks.STANDING_SIGN -> Right((16, 0, 1)),
      Blocks.LOG -> Right((16, -1, 4)),
      Blocks.LOG2 -> Right((16, -1, 4)),
      Blocks.ACACIA_FENCE_GATE -> Right((-4, -1, 1)),
      Blocks.OAK_FENCE_GATE -> Right((-4, -1, 1)),
      Blocks.BIRCH_FENCE_GATE -> Right((-4, -1, 1)),
      Blocks.JUNGLE_FENCE_GATE -> Right((-4, -1, 1)),
      Blocks.DARK_OAK_FENCE_GATE -> Right((-4, -1, 1)),
      Blocks.SPRUCE_FENCE_GATE -> Right((-4, -1, 1)),
      Blocks.POWERED_COMPARATOR -> Right((-4, -1, 1)),
      Blocks.UNPOWERED_COMPARATOR -> Right((-4, -1, 1)),
      Blocks.POWERED_REPEATER -> Right((-4, -1, 1)),
      Blocks.UNPOWERED_REPEATER -> Right((-4, -1, 1)),
      Blocks.ANVIL -> Right((-2, -1, 1)),
      Blocks.PISTON -> Left(rotPiston),
      Blocks.STICKY_PISTON -> Left(rotPiston)
  )
  
  def addRotationBlock(block: Block, data:Either[Rotation, RotData]) = blocksRot(block) = data
  def hasRotation(block: Block): Boolean = blocksRot.contains(block)
  def rotateBlock(world: World, pos:BlockPos): Boolean = {
    val blockData = (world, pos, world.getBlockState(pos))
    
    if (hasRotation(blockData.block)) {
      val typ = blocksRot(blockData.block)
      if(typ.isLeft) typ.left.get(blockData) else rotSimple(typ.right.get, blockData)
    } else false
  }
  def rotSimple(rotData:RotData, blockData:IMLazyBlockData):Boolean = blockData.setState(blockData.newState(rotData.nextStep(blockData.meta)))
  def rotPiston(blockData:IMLazyBlockData):Boolean = { 
    blockData.clearBlock
    blockData.notifyWorld
        
    rotSimple(if(blockData.meta >= 8) (6, if(blockData.meta + 1 < 14) (blockData.meta - 2) % 6 + 1  else 0, 1) else (6, 0, 1), blockData)
  }
  
  implicit class IMRotData(data:RotData) {
    def max = data._1
    def min = data._2
    def step = data._3
    def getMin(meta:Int) = if(min<0) if(max<0) getMax(meta) + max else meta % step else min
    def getMax(meta:Int) = if(max<0) Math.abs(max) * (1 + meta / Math.abs(max)) else max
    def nextStep(meta:Int) = if(meta+step<getMax(meta)) meta + step else getMin(meta)
  }
}