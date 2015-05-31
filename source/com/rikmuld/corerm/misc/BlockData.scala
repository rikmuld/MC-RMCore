package com.rikmuld.corerm.misc

import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import com.rikmuld.corerm.CoreUtils._
import java.util.Random
import net.minecraft.util.EnumFacing

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
    def material = state.getBlock.getMaterial
    def meta = block.getMetaFromState(state)
    def tile = world.getTileEntity(pos)
    def metarial = block.getMaterial
    def x = pos.getX
    def y = pos.getY
    def z = pos.getZ
    def setState(state:IBlockState) = world.setBlockState(pos, state)
    def setMeta(meta:Int) = world.setBlockState(pos, block.getStateFromMeta(meta))
    def relPos(xFlag:Int, yFlag:Int, zFlag:Int) = new BlockPos(x+xFlag, y+yFlag, z+zFlag)
    def setState(state:IBlockState, flag:Int) = world.setBlockState(pos, state, flag)
    def newState(meta:Int) = block.getStateFromMeta(meta)
    def notifyWorld = world.notifyNeighborsOfStateChange(pos, block)
    def update = world.markBlockForUpdate(pos)
    def clearBlock = world.setBlockToAir(pos)
    def dropInvItems = world.dropBlockItems(pos, new Random)
    def isReplaceable = block.isReplaceable(world, pos) 
    def solidBelow = World.doesBlockHaveSolidTopSurface(world, relPos(0, -1, 0))
    def canInstableStand = ((block == null) || isReplaceable) && solidBelow
    def toAir = world.setBlockToAir(pos)
    def isAir = world.isAirBlock(pos)
    def north = nw(pos.north)
    def south = nw(pos.south)
    def west = nw(pos.west)
    def east = nw(pos.east)
    def up = nw(pos.up)
    def down = nw(pos.down)
    def mkLazy = (world, pos, state)
    def unLazy = (world, pos)
    def nw(pos:BlockPos):BlockData = (world, pos)
    def nw(facing:EnumFacing):BlockData = (world, pos.offset(facing))
  }
}