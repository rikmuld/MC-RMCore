package com.rikmuld.corerm.objs

import java.util.Random
import com.rikmuld.corerm.CoreUtils._
import com.rikmuld.corerm.misc.WorldBlock._
import net.minecraft.block.Block
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import scala.collection.mutable.ListBuffer
import net.minecraft.block.state.BlockState
import scala.collection.JavaConversions._
import scala.reflect.ClassTag
import net.minecraft.util.EnumFacing
import net.minecraft.entity.player.EntityPlayer
import com.rikmuld.corerm.RMMod
import com.rikmuld.corerm.objs.PropType._
import net.minecraft.inventory.IInventory
import com.rikmuld.corerm.misc.WorldBlock._

class RMBlock(modId:String, info:ObjInfo) extends Block(info.getValue[Material](PropType.MATERIAL)) with RMCoreBlock {
  info.register(this, modId)
    
  def getInfo:ObjInfo = info
}

abstract class RMBlockContainer(modId:String, info:ObjInfo) extends BlockContainer(info.getValue[Material](PropType.MATERIAL)) with RMCoreBlock {
  info.register(this, modId)
    
  def getInfo:ObjInfo = info
  override def createNewTileEntity(world:World, meta:Int):RMTile = new RMTile
  override def getRenderType = 3
  override def breakBlock(world:World, pos:BlockPos, state:IBlockState) {
    if((world, pos).tile.isInstanceOf[IInventory])world.dropBlockItems(pos, new Random())
    super.breakBlock(world, pos, state)
  }
}

trait RMCoreBlock extends Block {
  getInfo.apply(this)
  
  def getInfo:ObjInfo
  override def isOpaqueCube() = !getInfo.hasProp(PropType.OPACITY)
  override def onBlockActivated(world: World, pos:BlockPos, state:IBlockState, player: EntityPlayer, side: EnumFacing, xHit: Float, yHit: Float, zHit: Float): Boolean = {
    if(!world.isRemote){
      if (getInfo.hasProp(GUITRIGGER)) {
        openGui((world, pos), player, getInfo.getProp[Properties.GuiTrigger](GUITRIGGER).value.asInstanceOf[Int])
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
    }
  }
  override def canPlaceBlockAt(world: World, pos:BlockPos) = (world, pos).canInstableStand
  override def onBlockAdded(world: World, pos:BlockPos, state:IBlockState) = if (!world.isRemote) dropIfCantStay((world, pos))
  override def onNeighborBlockChange(world: World, pos:BlockPos, state:IBlockState, block: Block) = if (!world.isRemote) dropIfCantStay((world, pos))
  def canStay(bd:BlockData): Boolean = bd.solidBelow
}

trait WithModel extends Block {
  override def isReplaceable(world: World, pos:BlockPos) = false
  override def isOpaqueCube = false
  override def isFullCube = false
}

trait WithProperties extends Block {   
  def getProps:Array[RMProp]
  def getProp(prop:IProperty):RMProp = getProps.find { rmProp => rmProp.prop.eq(prop) }.get
  protected override def createBlockState = new BlockState(this, getProps.map { rmProp => rmProp.prop }.toArray:_*)
  override def getStateFromMeta(meta:Int):IBlockState = {
    var state = getBlockState.getBaseState
    getProps.foreach { prop => state = state.withProperty(prop.prop, prop.fromIntData(prop.getBitData(meta) )) }
    state
  }
  override def getMetaFromState(state:IBlockState):Int = {
    var meta = 0
    getProps.foreach { prop => meta = prop.putBitData(prop.toIntData(state.getValue(prop.prop)), meta) }
    meta
  }
}

abstract class RMProp(val prop:IProperty, size:Int, bitPos:Int) {
  def getBitData(meta:Int) = meta.bitGet(bitPos, size)
  def putBitData(data:Int, meta:Int) = meta.bitPut(bitPos, data)
  def toIntAndPut(data:Comparable[_], meta:Int) = putBitData(toIntData(data), meta)
  def toIntData(data:Comparable[_]):Int
  def fromIntData(data:Int):Comparable[_]
}

class RMIntProp(prop:IProperty, size:Int, bitPos:Int) extends RMProp(prop, size, bitPos){
  def toIntData(data:Comparable[_]):Int = data.asInstanceOf[Int]
  def fromIntData(data:Int):Comparable[_] = data
}

class RMBoolProp(prop:IProperty, bitPos:Int) extends RMProp(prop, 1, bitPos){
  def toIntData(data:Comparable[_]):Int = data.asInstanceOf[Boolean].intValue
  def fromIntData(data:Int):Comparable[_] = data==1
}

class RMFacingHorizontalProp(prop:IProperty, bitPos:Int) extends RMProp(prop, 2, bitPos) {
  def toIntData(data:Comparable[_]):Int = data.asInstanceOf[EnumFacing].getHorizontalIndex
  def fromIntData(data:Int):Comparable[_] = EnumFacing.getHorizontal(data)
}