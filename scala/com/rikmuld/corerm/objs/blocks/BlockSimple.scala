package com.rikmuld.corerm.objs.blocks

import com.rikmuld.corerm.gui.GuiHelper
import com.rikmuld.corerm.objs.Properties._
import com.rikmuld.corerm.objs.{ObjDefinition, States}
import com.rikmuld.corerm.tileentity.TileEntitySimple
import com.rikmuld.corerm.utils.WorldUtils
import net.minecraft.block.Block
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.util.{BlockRenderLayer, EnumBlockRenderType, EnumFacing, EnumHand}
import net.minecraft.world.{IBlockAccess, World}

object BlockSimple {
  final val BOUNDS_EMPTY =
    new AxisAlignedBB(0, 0, 0, 0, 0, 0)
}

trait BlockSimple extends Block {
  getInfo.apply(this, getModId)
  getStates.foreach(state =>
    setInitialState(state.getDefaultState(getBlockState))
  )

  def setInitialState(state: IBlockState): Unit

  def getModId: String

  def getInfo: ObjDefinition

  private def getStates: Option[States] =
    getInfo.get(classOf[BlockStates]).map(_.states)

  def setState[A](world: World, pos: BlockPos, property: String, data: A): Unit =
    world.setBlockState(pos,
      getStates.get.set(property, data, world.getBlockState(pos))
    )

  def setState[A](state: IBlockState, property: String, data: A): IBlockState =
    getStates.get.set(property, data, state)

  def getState[A](world: IBlockAccess, pos: BlockPos, property: String): Option[A] =
    getState(world.getBlockState(pos), property)

  def getState[A](state: IBlockState, property: String): Option[A] =
    getStates.get.get(property, state)

  def getBool(world: IBlockAccess, pos: BlockPos, property: String): Boolean =
    getBool(world.getBlockState(pos), property)

  def getBool(state: IBlockState, property: String): Boolean =
    getState(state, property).getOrElse(false)

  def getFacing(world: IBlockAccess, pos: BlockPos): EnumFacing =
    getFacing(world.getBlockState(pos))

  def getFacing(state: IBlockState): EnumFacing =
    getDirection(state, "facing")

  def getInt(world: IBlockAccess, pos: BlockPos, property: String): Int =
    getInt(world.getBlockState(pos), property)

  def getInt(state: IBlockState, property: String): Int =
    getState(state, property).getOrElse(-1)

  def getDirection(world: World, pos: BlockPos, property: String): EnumFacing =
    getDirection(world.getBlockState(pos), property)

  def getDirection(state: IBlockState, property: String): EnumFacing =
    getState(state, property).getOrElse(EnumFacing.SOUTH)

  protected override def createBlockState: BlockStateContainer =
    getStates.fold(super.createBlockState)(state =>
      state.createState(this)
    )

  override def createTileEntity(world: World, state: IBlockState): TileEntity =
    getInfo.get(classOf[TileEntityClass[_ <: TileEntitySimple]]).fold[TileEntity](null)(
      tile => tile.tile.newInstance()
    )

  override def hasTileEntity(state: IBlockState): Boolean =
    getInfo.has(classOf[TileEntityClass[_ <: TileEntitySimple]])

  override def getStateFromMeta(meta:Int):IBlockState =
    getStates.fold(getDefaultState)(states => states.fromMeta(getBlockState, meta))

  override def getMetaFromState(state:IBlockState):Int =
    getStates.fold(0)(states => states.toMeta(state))


  //TODO for client only guis this may not work properly, using !world.remote check and force open may be better
  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState,
                                player: EntityPlayer, hand:EnumHand, side: EnumFacing,
                                xHit: Float, yHit: Float, zHit: Float): Boolean =

    getInfo.get(classOf[GuiTrigger]).fold(false)(gui => {
      GuiHelper.openGui(gui.id, player, pos)
      true
    })

  override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState,
                               placer: EntityLivingBase, stack: ItemStack): Unit = {

    //TODO make sure that if no item metadata it spawns the block with state default

    getStates.foreach(states =>
      // TODO only works for horizontal now, switch for other types
      world.setBlockState(pos, states.set("facing", placer.getHorizontalFacing, state))
    )
  }

  override def getRenderType(state: IBlockState): EnumBlockRenderType =
    if(getInfo.has(Invisible) | getInfo.has(Air))
      EnumBlockRenderType.INVISIBLE
    else if(getInfo.has(HasTileModel))
      EnumBlockRenderType.ENTITYBLOCK_ANIMATED
    else
      EnumBlockRenderType.MODEL

  override def isOpaqueCube(state: IBlockState): Boolean =
    !getInfo.has(Air) &&
      !getInfo.has(Invisible) &&
      !getInfo.has(NonCube) &&
      !getInfo.get(classOf[LightOpacity]).fold(false)(_.opacity != 255)

  override def isFullCube(state: IBlockState): Boolean =
    !getInfo.has(NonCube) && !getInfo.has(Air)

  override def getBlockLayer: BlockRenderLayer =
    getInfo.get(classOf[RenderType]).fold(BlockRenderLayer.SOLID)(_.layer)

  override def getCollisionBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos): AxisAlignedBB =
    if(getInfo.has(Air) || getInfo.has(NoCollision)) BlockSimple.BOUNDS_EMPTY
    else super.getCollisionBoundingBox(state, world, pos)

  override def getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB =
    if(getInfo.has(Air)) BlockSimple.BOUNDS_EMPTY
    else super.getBoundingBox(state, source, pos)

  override def canPlaceBlockAt(world: World, pos:BlockPos):Boolean =
    super.canPlaceBlockAt(world, pos) && canStay(world, pos)

  override def neighborChanged(state:IBlockState, world:World, pos:BlockPos, block:Block, fromPos: BlockPos): Unit =
    dropIfCantStay(world, pos)

  def dropIfCantStay(world: World, pos:BlockPos): Boolean =
    if (canStay(world, pos)) false
    else {
      world.destroyBlock(pos, true)
      true
    }

  override def breakBlock(world: World, pos: BlockPos, state: IBlockState): Unit = {
    WorldUtils.dropBlockItems(world, pos)

    super.breakBlock(world, pos, state)
  }

  def canStay(world: World, pos:BlockPos): Boolean =
    if(getInfo.has(Unstable))
      world.isSideSolid(pos.down, EnumFacing.UP)
    else
      true
}
