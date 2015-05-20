package com.rikmuld.corerm.misc

import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import com.rikmuld.corerm.CoreUtils._
import java.util.Random

object WorldBlock {
  type BlockData = (World, BlockPos)
  type LazyBlockData = (World, BlockPos, IBlockState)

  implicit class IMBlockData(data:BlockData) extends BlockFunctions {
    val world = data._1
    val pos = data._2
    val state = world.getBlockState(pos)
  }
  
  implicit class IMLazyBlockData(data:LazyBlockData) extends BlockFunctions {
    val world = data._1
    val pos = data._2
    val state = data._3
  }
  
  trait BlockFunctions {
    val world:World
    val state:IBlockState
    val pos:BlockPos
    
    def block = state.getBlock
    def meta = block.getMetaFromState(state)
    def tile = world.getTileEntity(pos)
    def metarial = block.getMaterial
    def x = pos.getX
    def y = pos.getY
    def z = pos.getZ
    def setState(state:IBlockState) = world.setBlockState(pos, state)
    def setState(state:IBlockState, flag:Int) = world.setBlockState(pos, state, flag)
    def newState(meta:Int) = block.getStateFromMeta(meta)
    def notifyWorld = world.notifyNeighborsOfStateChange(pos, block)
    def clearBlock = world.setBlockToAir(pos)
    def dropInvItems = world.dropBlockItems(pos, new Random)
    def isReplaceable = block.isReplaceable(world, pos) 
    
    def newBD(pos:BlockPos):BlockData = (world, pos)
    def newBD(world:World):BlockData = (world, pos)
    def newBDL(pos:BlockPos):LazyBlockData = (world, pos, state)
    def newBDL(world:World):LazyBlockData = (world, pos, state)
    def newBDL(state:IBlockState):LazyBlockData = (world, pos, state)
  }
}