package com.rikmuld.corerm.objs.items

import java.util.List

import com.rikmuld.corerm.RMMod
import com.rikmuld.corerm.objs.PropType._
import com.rikmuld.corerm.objs.{ObjInfo, Properties}
import com.rikmuld.corerm.utils.DataContainer
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item._
import net.minecraft.util.{ActionResult, EnumHand, NonNullList}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

class RMItem(val modId:String, info:ObjInfo) extends RMCoreItem {
  def getItemInfo:ObjInfo = info
  def getModId = modId
}

class RMItemBlock(val modId:String, info:ObjInfo, block:Block) extends ItemBlock(block) with RMCoreItem {
  def getItemInfo:ObjInfo = info
  def getModId = modId
}

class RMItemFood(val modId:String, info:ObjInfo) extends ItemFood(info.getValue(HEAL), info.getValue(SATURATION), info.getValue(WOLFMEAT)) with RMCoreItem {
  def getItemInfo:ObjInfo = info
  def getModId = modId
}

class RMItemArmor(val modId:String, info:ObjInfo) extends ItemArmor(info.getValue(ARMORMAT), 0, info.getValue(ARMORTYPE))  with RMCoreItem {
  maxStackSize = 1

  def getItemInfo:ObjInfo = info
  def getModId = modId

  override def getArmorTexture(stack: ItemStack, entity: Entity, slot: EntityEquipmentSlot, layer: String): String = getItemInfo.getValue(ARMORTEX)
}

abstract trait RMCoreItem extends Item {  
  getItemInfo.apply(this)
  
  val hasMeta = getItemInfo.hasProp(METADATA)
  val meta = if(!hasMeta) null else getItemInfo.getValue[Array[String]](METADATA)
  if(getItemInfo.hasProp(FORCESUBTYPE))setHasSubtypes(getItemInfo.getValue(FORCESUBTYPE))
  else if(getItemInfo.hasProp(METADATA))setHasSubtypes(true)

  def getItemInfo: ObjInfo
  def getModId: String

  override def getMetadata(damageValue: Int): Int = if(hasMeta) damageValue else 0
  override def getUnlocalizedName(stack:ItemStack):String = if(!getItemInfo.hasProp(METADATA)) getUnlocalizedName else "item." + getModId + ":" + getItemInfo.getValue[String](NAME) + "_" + meta(stack.getMetadata)
  @SideOnly(Side.CLIENT)
  override def getSubItems(itemIn:Item, tab:CreativeTabs, subItems:NonNullList[ItemStack]) {
    if(hasMeta){
      for(i <- 0 until meta.length) subItems.asInstanceOf[List[ItemStack]].add(new ItemStack(itemIn, 1, i)) 
    } else subItems.asInstanceOf[List[ItemStack]].add(new ItemStack(itemIn, 1, 0))
  }
  override def onItemRightClick(world: World, player: EntityPlayer, hand:EnumHand): ActionResult[ItemStack] = {
    if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
      val stack = player.getHeldItem(hand)
      var success = false
      if(getItemInfo.hasProp(GUITRIGGER_META)){
        val guis = getItemInfo.getProp[Properties.GuiTrigger](GUITRIGGER_META).value.asInstanceOf[Array[(Int, DataContainer[Int])]]
        val damage = stack.getItemDamage
        
        val gui = guis.filter { ids => ids._1 == damage }

        if(gui.length>0){
          player.openGui(RMMod, gui(0)._2.get.get, world, 0, 0, 0)
          success = true
        }
      }
      if(getItemInfo.hasProp(GUITRIGGER)&&(!success)){
        val gui = getItemInfo.getProp[Properties.GuiTrigger](GUITRIGGER).value.asInstanceOf[DataContainer[Int]].get.get
        player.openGui(RMMod, gui, world, 0, 0, 0)
      }
    } 
    super.onItemRightClick(world, player, hand)
  }
}