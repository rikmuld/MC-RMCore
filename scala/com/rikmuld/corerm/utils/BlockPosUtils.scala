package com.rikmuld.corerm.utils

import com.rikmuld.corerm.utils.WorldUtils._
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

case class BlockPosUtils(world:World, pos:BlockPos) {
  def state: IBlockState =
    world.getBlockState(pos)

  def block: Block =
    state.getBlock

  def meta: Int =
    block.getMetaFromState(state)

  def tile: TileEntity =
    world.getTileEntity(pos)

  def x: Int =
    pos.getX

  def y: Int =
    pos.getY

  def z: Int =
    pos.getZ

  def relative(dx:Int, dy:Int, dz:Int): BlockPos =
    new BlockPos(x + dx, y + dy, z + dz)

  def setState(state:IBlockState): Boolean =
    world.setBlockState(pos, state)

  def notifyNeighbors(): Unit =
    world.notifyNeighborsOfStateChange(pos, block, true)

  def updateBlock(): Unit =
    world.notifyBlockUpdate(pos, state, state, 3)

  def dropItems(): Unit =
    dropBlockItems(world, pos)

  def isReplaceable: Boolean =
    block.isReplaceable(world, pos)

  def solidBelow: Boolean =
    world.isSideSolid(pos.down, EnumFacing.UP)

  def canUnstableStand: Boolean =
    isReplaceable && solidBelow

  def toAir: Boolean =
    world.setBlockToAir(pos)

  def isAir: Boolean =
    world.isAirBlock(pos)

  def north:BlockPosUtils =
    newFrom(pos.north)

  def south:BlockPosUtils =
    newFrom(pos.south)

  def west:BlockPosUtils =
    newFrom(pos.west)

  def east:BlockPosUtils =
    newFrom(pos.east)

  def up:BlockPosUtils =
    newFrom(pos.up)

  def down:BlockPosUtils =
    newFrom(pos.down)

  def newFrom(pos:BlockPos):BlockPosUtils =
    BlockPosUtils(world, pos)

  def newFrom(facing:EnumFacing):BlockPosUtils =
    BlockPosUtils(world, pos.offset(facing))
}