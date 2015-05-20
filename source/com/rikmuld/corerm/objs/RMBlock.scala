package com.rikmuld.corerm.objs

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.BlockContainer
import net.minecraft.world.World

class RMBlock(modId:String, info:ObjInfo) extends Block(info.getValue[Material](PropType.MATERIAL)) with RMCoreBlock {
  info.register(this, modId)
    
  def getInfo:ObjInfo = info
}

abstract class RMBlockContainer(modId:String, info:ObjInfo) extends BlockContainer(info.getValue[Material](PropType.MATERIAL)) with RMCoreBlock {
  info.register(this, modId)
    
  def getInfo:ObjInfo = info
  override def createNewTileEntity(world:World, meta:Int):RMTile = new RMTile
}

trait RMCoreBlock extends Block {
  getInfo.apply(this)
  
  def getInfo:ObjInfo
  override def isOpaqueCube() = !getInfo.hasProp(PropType.OPACITY)
}

trait withInstable extends Block {
  //TODO: ADD
}

trait withRotation extends Block {
  //TODO: ADD
}