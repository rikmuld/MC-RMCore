package com.rikmuld.corerm.objs.items

import com.rikmuld.corerm.objs.ObjDefinition
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.util.{ActionResult, EnumHand}
import net.minecraft.world.World

class ItemBlockRM(modId:String, info: ObjDefinition, block:Block) extends ItemBlock(block) with ItemSimple {
  def getItemInfo: ObjDefinition =
    info

  def getModId: String =
    modId

  override def onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] =
    super[ItemBlock].onItemRightClick(world, player, hand)
}