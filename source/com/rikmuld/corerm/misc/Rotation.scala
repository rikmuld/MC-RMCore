package com.rikmuld.corerm.misc

import net.minecraft.block.Block
import scala.collection.mutable.HashMap
import net.minecraft.init.Blocks
import scala.actors.threadpool.Arrays
import net.minecraft.world.World
import com.rikmuld.corerm.objs.RMTileWithRot
import net.minecraft.util.BlockPos
import net.minecraft.block.state.BlockState
import net.minecraft.block.state.IBlockState
import net.minecraft.block.properties.IProperty
import net.minecraft.block.BlockFurnace
import com.rikmuld.corerm.misc.WorldBlock._
import net.minecraft.block.BlockPistonBase

object Rotation {
  private type RotData = (Int, Int, Int)
  private type Rotation = (IMLazyBlockData) => Boolean
  
  val blocksRot: HashMap[Block, Either[Rotation, RotData]] = HashMap(
      Blocks.furnace -> Right((6, 2, 1)),
      Blocks.lit_furnace -> Right((6, 2, 1)),
      Blocks.oak_stairs -> Right((8, 0, 1)),
      Blocks.stone_stairs -> Right((8, 0, 1)),
      Blocks.brick_stairs -> Right((8, 0, 1)),
      Blocks.stone_brick_stairs -> Right((8, 0, 1)),
      Blocks.nether_brick_stairs -> Right((8, 0, 1)),
      Blocks.sandstone_stairs -> Right((8, 0, 1)),
      Blocks.spruce_stairs -> Right((8, 0, 1)),
      Blocks.birch_stairs -> Right((8, 0, 1)),
      Blocks.jungle_stairs -> Right((8, 0, 1)),
      Blocks.quartz_stairs -> Right((8, 0, 1)),
      Blocks.acacia_stairs -> Right((8, 0, 1)),
      Blocks.dark_oak_stairs -> Right((8, 0, 1)),
      Blocks.red_sandstone_stairs -> Right((8, 0, 1)),
      Blocks.chest -> Right((6, 2, 1)),
      Blocks.trapped_chest -> Right((6, 2, 1)),
      Blocks.ender_chest -> Right((6, 2, 1)),
      Blocks.dispenser -> Right((6, 0, 1)),
      Blocks.dropper -> Right((6, 0, 1)),
      Blocks.pumpkin -> Right((4, 0, 1)),
      Blocks.lit_pumpkin -> Right((4, 0, 1)),
      Blocks.standing_sign -> Right((16, 0, 1)),
      Blocks.log -> Right((16, -1, 4)),
      Blocks.log2 -> Right((16, -1, 4)),
      Blocks.acacia_fence_gate -> Right((-4, -1, 1)),
      Blocks.oak_fence_gate -> Right((-4, -1, 1)),
      Blocks.birch_fence_gate -> Right((-4, -1, 1)),
      Blocks.jungle_fence_gate -> Right((-4, -1, 1)),
      Blocks.dark_oak_fence_gate -> Right((-4, -1, 1)),
      Blocks.spruce_fence_gate -> Right((-4, -1, 1)),
      Blocks.powered_comparator -> Right((-4, -1, 1)),
      Blocks.unpowered_comparator -> Right((-4, -1, 1)),
      Blocks.powered_repeater -> Right((-4, -1, 1)),
      Blocks.unpowered_repeater -> Right((-4, -1, 1)),
      Blocks.anvil -> Right((-2, -1, 1)),
      Blocks.piston -> Left(rotPiston),
      Blocks.sticky_piston -> Left(rotPiston)
  )
  
  def hasRotation(block: Block): Boolean = blocksRot.contains(block)
  def rotateBlock(world: World, pos:BlockPos): Boolean = {
    val blockData = (world, pos, world.getBlockState(pos))
    
    if (hasRotation(blockData.block)) {
      val typ = blocksRot(blockData.block)
      if(typ.isLeft) typ.left.get(blockData) else rotSimple(typ.right.get, blockData)
    } else if (world.getTileEntity(pos).isInstanceOf[RMTileWithRot]&&world.getTileEntity(pos).asInstanceOf[RMTileWithRot].getCanChangeRotation) {
      world.getTileEntity(pos).asInstanceOf[RMTileWithRot].cycleRotation
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