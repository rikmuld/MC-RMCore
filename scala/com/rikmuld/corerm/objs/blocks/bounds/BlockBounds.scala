package com.rikmuld.corerm.objs.blocks.bounds

import java.util.Random

import com.rikmuld.corerm.objs.ObjDefinition
import com.rikmuld.corerm.objs.Properties.{BlockClass, Invisible, TileEntityClass}
import com.rikmuld.corerm.objs.blocks.{BlockRM, BlockSimple}
import com.rikmuld.corerm.tileentity.TileEntityBounds
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.{AxisAlignedBB, BlockPos, RayTraceResult}
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.{IBlockAccess, World}

object BlockBounds {
  val BOUNDS = new ObjDefinition(
    Invisible,
    BlockClass(classOf[BlockBounds]),
    TileEntityClass(classOf[TileEntityBounds])
  )
}

class BlockBounds(modId:String, info:ObjDefinition) extends BlockRM(modId, info) {
  override def getCollisionBoundingBox(state:IBlockState, world: IBlockAccess, pos:BlockPos): AxisAlignedBB =
    delegate(world, pos, BlockSimple.BOUNDS_EMPTY)((_, _, tile) => tile.getCollisionBounds)

  override def getBoundingBox(state:IBlockState, world: IBlockAccess, pos:BlockPos):AxisAlignedBB =
    delegate(world, pos, BlockSimple.BOUNDS_EMPTY)((_, _, tile) => tile.getBounds)

  override def quantityDropped(par1Random: Random): Int =
    0

  override def breakBlock(world: World, pos:BlockPos, state: IBlockState): Unit = {
    delegate[Unit](world, pos, Unit)((_, pos, _) =>
      world.destroyBlock(pos, true)
    )

    super.breakBlock(world, pos, state)
  }

  override def getBlockHardness(state:IBlockState, world: World, pos:BlockPos): Float =
    delegate(world, pos, 0f)((state, pos, _) =>
      state.getBlockHardness(world, pos)
    )

  override def getPickBlock(state:IBlockState, target: RayTraceResult,
                            world: World, pos:BlockPos, player:EntityPlayer): ItemStack =
    delegate(world, pos, ItemStack.EMPTY)((state, pos, _) =>
      state.getBlock.getPickBlock(state, target, world, pos, player)
    )

  override def onBlockActivated(world: World, pos:BlockPos, state:IBlockState,
                                player: EntityPlayer, hand:EnumHand, side: EnumFacing,
                                xHit: Float, yHit: Float, zHit: Float): Boolean =
    delegate(world, pos, false)((state, pos, _) =>
      state.getBlock.onBlockActivated(world, pos, state, player, hand, side, xHit, yHit, zHit)
    )

  override def neighborChanged(state:IBlockState, world:World, pos:BlockPos, block:Block, fromPos: BlockPos): Unit =
    delegate[Unit](world, pos, Unit)((state, pos, _) =>
      state.neighborChanged(world, pos, block, fromPos)
    )

  def delegate[A](world: IBlockAccess, pos: BlockPos, default: A)(f: (IBlockState, BlockPos, TileEntityBounds) => A): A =
    world.getTileEntity(pos) match {
      case bounds: TileEntityBounds =>
        f(world.getBlockState(bounds.getBasePos), bounds.getBasePos, bounds)
      case _ =>
        default
    }
}