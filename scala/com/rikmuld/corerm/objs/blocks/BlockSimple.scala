package com.rikmuld.corerm.objs.blocks

import com.rikmuld.corerm.gui.GuiHelper
import com.rikmuld.corerm.objs.Properties._
import com.rikmuld.corerm.objs.{ObjDefinition, States}
import com.rikmuld.corerm.utils.BlockData
import net.minecraft.block.Block
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumBlockRenderType, EnumFacing, EnumHand}
import net.minecraft.world.{IBlockAccess, World}

trait BlockSimple extends Block {
  getInfo.apply(this, getModId)

  val states: Option[States] =
    getInfo.get(classOf[BlockStates]).map(_.states)

  states.foreach(state =>
    setDefaultState(state.getDefaultState(getBlockState))
  )

  def getModId: String

  def getInfo: ObjDefinition

  def setState[A](world: World, pos: BlockPos, property: String, data: A): Unit =
    world.setBlockState(pos,
      states.get.set(property, data, world.getBlockState(pos))
    )

  def getState[A](world: World, pos: BlockPos, property: String): Option[A] =
    getState(world.getBlockState(pos), property)

  def getState[A](state: IBlockState, property: String): Option[A] =
    states.get.get(property, state)

  protected override def createBlockState: BlockStateContainer =
    states.fold(super.createBlockState)(state =>
      state.createState(this)
    )

  override def createTileEntity(world: World, state: IBlockState): TileEntity =
    getInfo.get(classOf[TileEntityClass[_ <: TileEntity]]).fold[TileEntity](null)(
      tile => tile.tile.newInstance()
    )

  override def getStateFromMeta(meta:Int):IBlockState =
    states.fold(getDefaultState)(states => states.fromMeta(getBlockState, meta))

  override def getMetaFromState(state:IBlockState):Int =
    states.fold(0)(states => states.toMeta(state))

  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState,
                                player: EntityPlayer, hand:EnumHand, side: EnumFacing,
                                xHit: Float, yHit: Float, zHit: Float): Boolean = {

    getInfo.get(classOf[GuiTrigger]).fold(false)(gui => {
      GuiHelper.forceOpenGui(gui.id, player, pos)
      true
    })
  }

  override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState,
                               placer: EntityLivingBase, stack: ItemStack): Unit = {

    states.foreach(states =>
      // TODO only works for horizontal now, switch for other types
      world.setBlockState(pos, states.set("facing", placer.getHorizontalFacing, state))
    )
  }

  override def getRenderType(state: IBlockState): EnumBlockRenderType =
    if(getInfo.has(classOf[Unstable.type]))
      EnumBlockRenderType.INVISIBLE
    else if(getInfo.has(classOf[HasTileModel.type ]))
      EnumBlockRenderType.ENTITYBLOCK_ANIMATED
    else
      EnumBlockRenderType.MODEL

  override def isNormalCube(state: IBlockState, world: IBlockAccess, pos: BlockPos): Boolean =
    !getInfo.has(classOf[NonCube.type])

  override def isOpaqueCube(state: IBlockState): Boolean =
    !getInfo.has(classOf[Invisible.type]) &&
      !getInfo.has(classOf[NonCube.type]) &&
      !getInfo.get(classOf[LightOpacity]).fold(false)(_ != 255)

  override def canPlaceBlockAt(world: World, pos:BlockPos):Boolean =
    super.canPlaceBlockAt(world, pos) && canStay(world, pos)

  override def neighborChanged(state:IBlockState, world:World, pos:BlockPos, block:Block, fromPos: BlockPos): Unit =
    dropIfCantStay(world, pos)

  def dropIfCantStay(world: World, pos:BlockPos): Boolean =
    if (canStay(world, pos)) false
    else {
      world.destroyBlock(pos, true)
      //breakBlock(world, pos, world.getBlockState(pos)) not sure if need to call this myself
      true
    }

  def canStay(world: World, pos:BlockPos): Boolean =
    if(getInfo.has(classOf[Unstable.type]))
      BlockData(world, pos).solidBelow
    else
      true
}
