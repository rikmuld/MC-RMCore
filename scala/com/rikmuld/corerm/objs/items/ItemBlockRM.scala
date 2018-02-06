package com.rikmuld.corerm.objs.items

import com.rikmuld.corerm.objs.ObjDefinition
import com.rikmuld.corerm.tileentity.TileEntitySimple
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{ActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World

class ItemBlockRM(modId:String, info: ObjDefinition, block:Block) extends ItemBlock(block) with ItemSimple {
  def getItemInfo: ObjDefinition =
    info

  def getModId: String =
    modId

  override def onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] =
    super[ItemBlock].onItemRightClick(world, player, hand)

  override def placeBlockAt(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, newState: IBlockState): Boolean =
    if(super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)){
      Option(world.getTileEntity(pos)).foreach(_.asInstanceOf[TileEntitySimple].init(stack, player, world, pos))
      true
    } else false
}