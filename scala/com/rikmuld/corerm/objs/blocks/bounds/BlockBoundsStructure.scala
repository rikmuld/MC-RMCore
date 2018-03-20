package com.rikmuld.corerm.objs.blocks.bounds

import com.rikmuld.corerm.objs.ObjDefinition
import com.rikmuld.corerm.objs.blocks.BlockRM
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.{IBlockAccess, World}

abstract class BlockBoundsStructure(modId:String, info:ObjDefinition) extends BlockRM(modId, info) {
  def getBoundsBlock: BlockBounds

  def getStructure(state: Option[IBlockState]): BoundsStructure

  override def getCollisionBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos): AxisAlignedBB =
    getStructure(Some(state)).getBounds.collisionBounds

  override def getBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos): AxisAlignedBB =
    getStructure(Some(state)).getBounds.bounds

  override def canPlaceBlockAt(world: World, pos: BlockPos): Boolean =
    getStructure(None).canBePlaced(world, pos)

  override def canStay(world: World, pos: BlockPos): Boolean =
    getStructure(Some(world.getBlockState(pos))).hadSolidUnderGround(world, pos)

  override def breakBlock(world: World, pos: BlockPos, state: IBlockState): Unit = {
    getStructure(Some(state)).destroyStructure(world, pos)

    super.breakBlock(world, pos, state)
  }

  //TODO call self from tile
  def createStructure(world: World, pos: BlockPos): Unit =
    if (!world.isRemote)
      getStructure(Some(world.getBlockState(pos))).createStructure(getBoundsBlock.getDefaultState, world, pos)
}
