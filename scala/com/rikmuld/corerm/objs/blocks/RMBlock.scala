package com.rikmuld.corerm.objs.blocks

import java.util.Random

import com.rikmuld.corerm.gui.GuiHelper
import com.rikmuld.corerm.objs.PropType._
import com.rikmuld.corerm.objs.items.RMItemBlock
import com.rikmuld.corerm.objs.{ObjInfo, PropType, Properties}
import com.rikmuld.corerm.tileentity.TileEntitySimple
import com.rikmuld.corerm.utils.CoreUtils._
import com.rikmuld.corerm.utils.MathUtils
import com.rikmuld.corerm.utils.WorldBlock._
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.block.{Block, BlockContainer}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumBlockRenderType, EnumFacing, EnumHand, ResourceLocation}
import net.minecraft.world.{IBlockAccess, World}

class RMBlock(modId:String, info:ObjInfo) extends Block(info.getValue[Material](PropType.MATERIAL)) with RMCoreBlock {
  def getInfo:ObjInfo = info
  def getModId: String = modId
  if(getInfo.hasProp(PropType.SOUND))this.setSoundType(getInfo.getValue(PropType.SOUND))
}

abstract class RMBlockContainer(modId:String, info:ObjInfo) extends BlockContainer(info.getValue[Material](PropType.MATERIAL)) with RMCoreBlock {
  def getInfo:ObjInfo = info
  def getModId: String = modId
  override def createNewTileEntity(world:World, meta:Int):TileEntitySimple = new TileEntitySimple
  override def getRenderType(state:IBlockState) = EnumBlockRenderType.MODEL
  override def breakBlock(world:World, pos:BlockPos, state:IBlockState) {
    if((world, pos).tile.isInstanceOf[IInventory])world.dropBlockItems(pos, new Random())
    super.breakBlock(world, pos, state)
  }
}

trait RMCoreBlock extends Block {
  getInfo.apply(this, getModId)

  def generateItem(): RMItemBlock =
    if (getInfo.hasProp(PropType.ITEMBLOCK))
      getInfo.getValue[Class[RMItemBlock]](PropType.ITEMBLOCK).getConstructor(classOf[Block]).newInstance(this)
    else classOf[RMItemBlock].getConstructor(classOf[String], classOf[ObjInfo], classOf[Block]).newInstance(getModId, getInfo, this)

  def getModId: String
  def getInfo:ObjInfo
  override def isOpaqueCube(state:IBlockState) = !getInfo.hasProp(PropType.OPACITY)
  override def onBlockActivated(world: World, pos:BlockPos, state:IBlockState, player: EntityPlayer, hand:EnumHand, side: EnumFacing, xHit: Float, yHit: Float, zHit: Float): Boolean = {
    if (getInfo.hasProp(GUITRIGGER)) {
      GuiHelper.forceOpenGui(getInfo.getProp[Properties.GuiTrigger](GUITRIGGER).value.asInstanceOf[ResourceLocation], player, pos)
      true
    } else false
  }
}

trait WithInstable extends Block {
  def dropIfCantStay(bd:BlockData) {
    if (!canStay(bd)) {
      breakBlock(bd.world, bd.pos, bd.state)
      dropBlockAsItemWithChance(bd.world, bd.pos, bd.state, 1, 1)
      bd.toAir
      bd.update
    } else couldStay(bd)
  }
  override def canPlaceBlockAt(world: World, pos:BlockPos) = (world, pos).canInstableStand && canStay((world, pos))
  override def onBlockAdded(world: World, pos:BlockPos, state:IBlockState) = if (!world.isRemote) dropIfCantStay((world, pos))
  override def neighborChanged(state:IBlockState, world:World, pos:BlockPos, block:Block, fromPos: BlockPos) = dropIfCantStay((world, pos))
  def canStay(bd:BlockData): Boolean = bd.solidBelow
  def couldStay(bd:BlockData) = {}
}

trait WithModel extends Block {
  override def isReplaceable(world: IBlockAccess, pos:BlockPos) = false
  override def isOpaqueCube(state:IBlockState) = false
  override def isFullCube(state:IBlockState) = false
  
  override def getRenderType(state:IBlockState) = EnumBlockRenderType.ENTITYBLOCK_ANIMATED
}

trait WithProperties extends Block {   
  def getProps:Array[RMProp]
  def getProp(prop:IProperty[_]):RMProp = getProps.find { rmProp => rmProp.prop.eq(prop) }.get
  protected override def createBlockState = new BlockStateContainer(this, getProps.map { rmProp => rmProp.prop }.toArray:_*)
  override def getStateFromMeta(meta:Int):IBlockState = {
    var state = getBlockState.getBaseState
    getProps.foreach { prop => state = state.withProperty(prop.prop, prop.fromIntData(prop.getBitData(meta) )) }
    state
  }
  override def getMetaFromState(state:IBlockState):Int = {
    var meta = 0
    getProps.foreach { prop =>
      val comp = state.getValue(prop.prop)
      val data = prop.toIntData(comp.asInstanceOf[Comparable[_]])
      meta = prop.putBitData(data, meta)
    }
    meta
  }
}

abstract class RMProp(val prop:IProperty[_], size:Int, bitPos:Int) {
  def getBitData(meta:Int) = MathUtils.bitGet(meta, bitPos, size)
  def putBitData(data:Int, meta:Int) = MathUtils.bitPut(meta, bitPos, data)
  def toIntAndPut(data:Comparable[_], meta:Int) = putBitData(toIntData(data), meta)
  def toIntData(data:Comparable[_]):Int
  def fromIntData[V](data:Int):V
}

class RMIntProp(prop:IProperty[java.lang.Integer], size:Int, bitPos:Int) extends RMProp(prop, size, bitPos){
  def toIntData(data:Comparable[_]):Int = data.asInstanceOf[Int]
  def fromIntData[V](data:Int):V = data.asInstanceOf[V]
}

class RMBoolProp(prop:IProperty[java.lang.Boolean], bitPos:Int) extends RMProp(prop, 1, bitPos){
  def toIntData(data:Comparable[_]):Int = MathUtils.toInt(data.asInstanceOf[Boolean])
  def fromIntData[V](data:Int):V = (data==1).asInstanceOf[V]
}

class RMFacingHorizontalProp(prop:IProperty[EnumFacing], bitPos:Int) extends RMProp(prop, 2, bitPos) {
  def toIntData(data:Comparable[_]):Int = data.asInstanceOf[EnumFacing].getHorizontalIndex
  def fromIntData[V](data:Int):V = EnumFacing.getHorizontal(data).asInstanceOf[V]
}