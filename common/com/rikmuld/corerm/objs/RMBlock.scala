package com.rikmuld.corerm.objs

import java.util.Random

import com.rikmuld.corerm.CoreUtils._
import com.rikmuld.corerm.misc.WorldBlock._
import net.minecraft.block.{Block, BlockBed, BlockContainer}
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.world.World

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import scala.reflect.ClassTag
import net.minecraft.util.EnumFacing
import net.minecraft.entity.player.EntityPlayer
import com.rikmuld.corerm.RMMod
import com.rikmuld.corerm.misc.DataContainer
import com.rikmuld.corerm.objs.PropType._
import net.minecraft.inventory.IInventory
import com.rikmuld.corerm.misc.WorldBlock._

import scala.collection.JavaConversions._
import net.minecraft.util.math.BlockPos
import net.minecraft.block.state.BlockStateBase
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.IBlockAccess
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumHand
import net.minecraft.item.ItemStack

class RMBlock(modId:String, info:ObjInfo) extends Block(info.getValue[Material](PropType.MATERIAL)) with RMCoreBlock {
  def getInfo:ObjInfo = info
  if(getInfo.hasProp(PropType.SOUND))this.setSoundType(getInfo.getValue(PropType.SOUND))
}

abstract class RMBlockContainer(modId:String, info:ObjInfo) extends BlockContainer(info.getValue[Material](PropType.MATERIAL)) with RMCoreBlock {
  def getInfo:ObjInfo = info
  override def createNewTileEntity(world:World, meta:Int):RMTile = new RMTile
  override def getRenderType(state:IBlockState) = EnumBlockRenderType.MODEL
  override def breakBlock(world:World, pos:BlockPos, state:IBlockState) {
    if((world, pos).tile.isInstanceOf[IInventory])world.dropBlockItems(pos, new Random())
    super.breakBlock(world, pos, state)
  }
}

trait RMCoreBlock extends Block {
  getInfo.apply(this)
  
  def getInfo:ObjInfo
  override def isOpaqueCube(state:IBlockState) = !getInfo.hasProp(PropType.OPACITY)
  override def onBlockActivated(world: World, pos:BlockPos, state:IBlockState, player: EntityPlayer, hand:EnumHand, side: EnumFacing, xHit: Float, yHit: Float, zHit: Float): Boolean = {
    if(!world.isRemote){
      if (getInfo.hasProp(GUITRIGGER)) {
        openGui((world, pos), player, getInfo.getProp[Properties.GuiTrigger](GUITRIGGER).value.asInstanceOf[DataContainer[Int]].get.get)
        true
      } else false
    } else true  
  }
  def openGui(bd:BlockData, player:EntityPlayer, id:Int) = player.openGui(RMMod, id, bd.world, bd.x, bd.y, bd.z)
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
  def getBitData(meta:Int) = meta.bitGet(bitPos, size)
  def putBitData(data:Int, meta:Int) = meta.bitPut(bitPos, data)
  def toIntAndPut(data:Comparable[_], meta:Int) = putBitData(toIntData(data), meta)
  def toIntData(data:Comparable[_]):Int
  def fromIntData[V](data:Int):V
}

class RMIntProp(prop:IProperty[java.lang.Integer], size:Int, bitPos:Int) extends RMProp(prop, size, bitPos){
  def toIntData(data:Comparable[_]):Int = data.asInstanceOf[Int]
  def fromIntData[V](data:Int):V = data.asInstanceOf[V]
}

class RMBoolProp(prop:IProperty[java.lang.Boolean], bitPos:Int) extends RMProp(prop, 1, bitPos){
  def toIntData(data:Comparable[_]):Int = data.asInstanceOf[Boolean].intValue
  def fromIntData[V](data:Int):V = (data==1).asInstanceOf[V]
}

class RMFacingHorizontalProp(prop:IProperty[EnumFacing], bitPos:Int) extends RMProp(prop, 2, bitPos) {
  def toIntData(data:Comparable[_]):Int = data.asInstanceOf[EnumFacing].getHorizontalIndex
  def fromIntData[V](data:Int):V = EnumFacing.getHorizontal(data).asInstanceOf[V]
}