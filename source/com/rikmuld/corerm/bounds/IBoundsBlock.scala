package com.rikmuld.corerm.bounds

import com.rikmuld.corerm.objs.RMBlock
import com.rikmuld.corerm.objs.WithModel
import com.rikmuld.corerm.objs.RMBlockContainer
import com.rikmuld.corerm.objs.ObjInfo
import net.minecraft.entity.EntityLivingBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.IBlockAccess
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraft.init.Blocks
import java.util.Random
import net.minecraft.entity.Entity
import net.minecraft.block.Block
import net.minecraft.dispenser.IBlockSource
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockPos
import com.rikmuld.corerm.misc.WorldBlock._
import net.minecraft.util.EnumFacing
import com.rikmuld.corerm.objs.RMTile

abstract trait IBoundsBlock extends RMBlockContainer with WithModel {
  override def getRenderType = -1
  override def addCollisionBoxesToList(world: World, pos:BlockPos, state:IBlockState, alignedBB: AxisAlignedBB, list: java.util.List[_], entity: Entity) {
    Option((world, pos).tile.asInstanceOf[TileBounds].bounds) map (bounds => bounds.setBlockCollision(this))
    super.addCollisionBoxesToList(world, pos, state, alignedBB, list, entity)
  }
  override def breakBlock(world: World, pos:BlockPos, state: IBlockState) {
    val tile = (world, pos).tile.asInstanceOf[TileBounds]
    val bd = (world, tile.basePos)
    if (!bd.isAir) bd.toAir
    super.breakBlock(world, pos, state)
  }
  override def createNewTileEntity(world: World, meta: Int): RMTile = new TileBounds
  override def getBlockHardness(world: World, pos:BlockPos): Float = {
    val tileFlag = Option((world, pos).tile)
    var tile:Option[TileBounds] = None
    
    if(tileFlag.isDefined&&tileFlag.get.isInstanceOf[TileBounds])tile = Some(tileFlag.get.asInstanceOf[TileBounds])
    
    var hardness = super.getBlockHardness(world, pos)
    tile map (tile => if(!tile.bd.isAir) hardness = (world, tile.basePos).block.getBlockHardness(world, tile.basePos))
    hardness
  }
  override def getPickBlock(target: MovingObjectPosition, world: World, pos:BlockPos): ItemStack = {
    val tile = (world, pos).tile.asInstanceOf[TileBounds]
    val bd = (world, tile.basePos)
    bd.block.getPickBlock(target, world, pos)
  }
  override def onBlockActivated(world: World, pos:BlockPos, state:IBlockState, player: EntityPlayer, side: EnumFacing, xHit: Float, yHit: Float, zHit: Float): Boolean = {
    val tile = (world, pos).tile.asInstanceOf[TileBounds]
    val bd = (world, tile.basePos)
    bd.block.onBlockActivated(world, bd.pos, bd.state, player, side, xHit, yHit, zHit)
  }
  override def onNeighborBlockChange(world: World, pos:BlockPos, state:IBlockState, block: Block) {
    val tile = (world, pos).tile.asInstanceOf[TileBounds]
    val bd = (world, tile.basePos)
    if (!bd.isAir) bd.block.onNeighborBlockChange(world, bd.pos, state, block)
  }
  override def quantityDropped(par1Random: Random): Int = 0
  override def setBlockBoundsBasedOnState(world: IBlockAccess, pos:BlockPos) {
    val tile = world.getTileEntity(pos).asInstanceOf[TileBounds]
    Option(tile.bounds) map (bounds => bounds.setBlockBounds(this))
  }
}