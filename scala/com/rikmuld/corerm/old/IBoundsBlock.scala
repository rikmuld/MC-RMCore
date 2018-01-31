package com.rikmuld.corerm.old

import java.util.Random

import com.rikmuld.corerm.tileentity.{TileEntityBounds, TileEntitySimple}
import com.rikmuld.corerm.utils.BlockData
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.{AxisAlignedBB, BlockPos, RayTraceResult}
import net.minecraft.util.{EnumBlockRenderType, EnumFacing, EnumHand}
import net.minecraft.world.{IBlockAccess, World}

object BoundsBlock {
  final val EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0)
}

abstract trait IBoundsBlock extends Block {
  override def getRenderType(state:IBlockState) = EnumBlockRenderType.INVISIBLE
  override def getCollisionBoundingBox(state:IBlockState, world: IBlockAccess, pos:BlockPos):AxisAlignedBB =
    if(world.getTileEntity(pos)!=null) 
      Option(world.getTileEntity(pos).asInstanceOf[TileEntityBounds].bounds).map(bounds => bounds.getBlockCollision).getOrElse(BoundsBlock.EMPTY)
    else BoundsBlock.EMPTY
      
  override def breakBlock(world: World, pos:BlockPos, state: IBlockState) {
    val base = world.getTileEntity(pos).asInstanceOf[TileEntityBounds].basePos
    world.setBlockToAir(base)
    super.breakBlock(world, pos, state)
  }
  override def createTileEntity(world: World, state: IBlockState): TileEntitySimple = new TileEntityBounds
  override def getBlockHardness(state:IBlockState, world: World, pos:BlockPos): Float = {
    var tile:Option[TileEntityBounds] = Option(world.getTileEntity(pos).asInstanceOf[TileEntityBounds])

    var hardness = super.getBlockHardness(state, world, pos)
    tile map (tile => hardness = world.getBlockState(tile.basePos).getBlock.getBlockHardness(state, world, tile.basePos))
    hardness
  }
  override def getPickBlock(state:IBlockState, target: RayTraceResult, world: World, pos:BlockPos, player:EntityPlayer): ItemStack = {
    val tile = world.getTileEntity(pos).asInstanceOf[TileEntityBounds]
    val bd = BlockData(world, tile.basePos)
    bd.block.getPickBlock(bd.state, target, world, pos, player)
  }
  override def onBlockActivated(world: World, pos:BlockPos, state:IBlockState, player: EntityPlayer, hand:EnumHand, side: EnumFacing, xHit: Float, yHit: Float, zHit: Float): Boolean = {
    val tile = world.getTileEntity(pos).asInstanceOf[TileEntityBounds]
    val bd = BlockData(world, tile.basePos)
    bd.block.onBlockActivated(world, bd.pos, bd.state, player, hand, side, xHit, yHit, zHit)
  }
  override def neighborChanged(state:IBlockState, world:World, pos:BlockPos, block:Block, fromPos: BlockPos) = {
    val tile = world.getTileEntity(pos)
    if (!world.isAirBlock(pos)) world.getBlockState(tile.asInstanceOf[TileEntityBounds].basePos).getBlock.neighborChanged(state, world, tile.asInstanceOf[TileEntityBounds].basePos, block, fromPos)
  }
  override def quantityDropped(par1Random: Random): Int = 0
  override def getBoundingBox(state:IBlockState, world: IBlockAccess, pos:BlockPos):AxisAlignedBB = 
    (Option(world.getTileEntity(pos).asInstanceOf[TileEntityBounds].bounds) map (bounds =>
      bounds.getBlockBounds)).getOrElse(BoundsBlock.EMPTY)
}