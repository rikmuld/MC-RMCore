package com.rikmuld.corerm.bounds

import com.rikmuld.corerm.objs.RMBlock
import com.rikmuld.corerm.objs.WithModel
import com.rikmuld.corerm.objs.RMBlockContainer
import com.rikmuld.corerm.objs.ObjInfo
import net.minecraft.entity.EntityLivingBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.IBlockAccess
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraft.init.Blocks
import java.util.Random
import net.minecraft.entity.Entity
import net.minecraft.block.Block
import net.minecraft.dispenser.IBlockSource
import net.minecraft.block.state.IBlockState
import com.rikmuld.corerm.misc.WorldBlock._
import net.minecraft.util.EnumFacing
import com.rikmuld.corerm.objs.RMTile
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumBlockRenderType

object BoundsBlock {
  final val EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0)
}

abstract trait IBoundsBlock extends RMBlockContainer with WithModel {
  override def getRenderType(state:IBlockState) = EnumBlockRenderType.INVISIBLE
  override def getCollisionBoundingBox(state:IBlockState, world: IBlockAccess, pos:BlockPos):AxisAlignedBB =
    if(world.getTileEntity(pos)!=null) 
      Option(world.getTileEntity(pos).asInstanceOf[TileBounds].bounds).map(bounds => bounds.getBlockCollision).getOrElse(BoundsBlock.EMPTY)
    else BoundsBlock.EMPTY
      
  override def breakBlock(world: World, pos:BlockPos, state: IBlockState) {
    val tile = (world, pos).tile.asInstanceOf[TileBounds]
    val bd = (world, tile.basePos)
    if (!bd.isAir) bd.toAir
    super.breakBlock(world, pos, state)
  }
  override def createTileEntity(world: World, state: IBlockState): RMTile = new TileBounds
  override def getBlockHardness(state:IBlockState, world: World, pos:BlockPos): Float = {
    val tileFlag = Option((world, pos).tile)
    var tile:Option[TileBounds] = None
    
    if(tileFlag.isDefined&&tileFlag.get.isInstanceOf[TileBounds])tile = Some(tileFlag.get.asInstanceOf[TileBounds])
    
    var hardness = super.getBlockHardness(state, world, pos)
    tile map (tile => 
      if(!tile.bd.isAir) hardness = (world, tile.basePos).block.getBlockHardness(state, world, tile.basePos))
    hardness
  }
  override def getPickBlock(state:IBlockState, target: RayTraceResult, world: World, pos:BlockPos, player:EntityPlayer): ItemStack = {
    val tile = (world, pos).tile.asInstanceOf[TileBounds]
    val bd = (world, tile.basePos)
    bd.block.getPickBlock(bd.state, target, world, pos, player)
  }
  override def onBlockActivated(world: World, pos:BlockPos, state:IBlockState, player: EntityPlayer, hand:EnumHand, side: EnumFacing, xHit: Float, yHit: Float, zHit: Float): Boolean = {
    val tile = (world, pos).tile.asInstanceOf[TileBounds]
    val bd = (world, tile.basePos)
    bd.block.onBlockActivated(world, bd.pos, bd.state, player, hand, side, xHit, yHit, zHit)
  }
  //TODO test neighhbor changes, else fix deprication
  override def neighborChanged(state:IBlockState, world:World, pos:BlockPos, block:Block, fromPos: BlockPos) = {
    val tile = world.getTileEntity(pos)
    if (!world.isAirBlock(pos)) world.getBlockState(tile.asInstanceOf[TileBounds].basePos).getBlock.neighborChanged(state, world, tile.asInstanceOf[TileBounds].basePos, block, fromPos)
  }
  override def quantityDropped(par1Random: Random): Int = 0
  override def getBoundingBox(state:IBlockState, world: IBlockAccess, pos:BlockPos):AxisAlignedBB = 
    (Option(world.getTileEntity(pos).asInstanceOf[TileBounds].bounds) map (bounds => 
      bounds.getBlockBounds)).getOrElse(BoundsBlock.EMPTY)
}