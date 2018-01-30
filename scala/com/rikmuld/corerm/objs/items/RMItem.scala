package com.rikmuld.corerm.objs.items

import java.util.List

import com.rikmuld.corerm.gui.GuiHelper
import com.rikmuld.corerm.objs.PropType._
import com.rikmuld.corerm.objs.Properties.{Metadata, Tab}
import com.rikmuld.corerm.objs.{ObjInfo, PropType, Properties}
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.{ModelBakery, ModelResourceLocation}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item._
import net.minecraft.util.{ActionResult, EnumHand, NonNullList, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

class RMItem(val modId:String, info:ObjInfo) extends RMCoreItem {
  def getItemInfo:ObjInfo = info
  def getModId = modId
}

class RMItemBlock(val modId:String, info:ObjInfo, block:Block) extends ItemBlock(block) with RMCoreItem {
  def getItemInfo: ObjInfo = info

  def getModId = modId

  override def onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] =
    super[ItemBlock].onItemRightClick(world, player, hand)
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
  getItemInfo.apply(this, getModId)

  val hasMeta = getItemInfo.hasProp(METADATA)
  val meta = if(!hasMeta) null else getItemInfo.getValue[Array[String]](METADATA)
  if(getItemInfo.hasProp(FORCESUBTYPE))setHasSubtypes(getItemInfo.getValue(FORCESUBTYPE))
  else if(getItemInfo.hasProp(METADATA))setHasSubtypes(true)

  def getItemInfo: ObjInfo
  def getModId: String

  override def getMetadata(damageValue: Int): Int = if(hasMeta) damageValue else 0
  override def getUnlocalizedName(stack:ItemStack):String = if(!getItemInfo.hasProp(METADATA)) getUnlocalizedName else "item." + getModId + ":" + getItemInfo.getValue[String](NAME) + "_" + meta(stack.getMetadata)
  override def getCreativeTabs: Array[CreativeTabs] =
    Array(getItemInfo.getProp(TAB).asInstanceOf[Tab].tab, CreativeTabs.SEARCH)

  @SideOnly(Side.CLIENT)
  def registerRenders(): Unit =
    if(getItemInfo.hasProp(PropType.METADATA)){
      val meta = getItemInfo.getProp[Metadata](PropType.METADATA)
      for(i <- meta.names.indices){
        ModelBakery.registerItemVariants(this, new ResourceLocation(getModId, getItemInfo.getValue[String](PropType.NAME) + "_" + meta.getName(i)))
        ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(new ResourceLocation(getModId, getItemInfo.getValue[String](PropType.NAME)) + "_" + meta.getName(i), "inventory"))
      }
    } else ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(new ResourceLocation(getModId, getItemInfo.getValue[String](PropType.NAME)), "inventory"))

  override def getSubItems(tab:CreativeTabs, subItems:NonNullList[ItemStack]) {
    if(!getCreativeTabs.contains(tab)) return

    if(hasMeta){
      for(i <- 0 until meta.length) subItems.asInstanceOf[List[ItemStack]].add(new ItemStack(this, 1, i))
    } else subItems.asInstanceOf[List[ItemStack]].add(new ItemStack(this, 1, 0))
  }
  override def onItemRightClick(world: World, player: EntityPlayer, hand:EnumHand): ActionResult[ItemStack] = {
    if (hand == EnumHand.MAIN_HAND) {
      val stack = player.getHeldItem(hand)
      var success = false
      if(getItemInfo.hasProp(GUITRIGGER_META)){
        val guis = getItemInfo.getProp[Properties.GuiTrigger](GUITRIGGER_META).value.asInstanceOf[Array[(Int, ResourceLocation)]]
        val damage = stack.getItemDamage
        
        val gui = guis.filter { ids => ids._1 == damage }

        if(gui.length>0){
          GuiHelper.openGui(gui(0)._2, player)
          success = true
        }
      }
      if(getItemInfo.hasProp(GUITRIGGER)&&(!success)){
        val gui = getItemInfo.getProp[Properties.GuiTrigger](GUITRIGGER).value.asInstanceOf[ResourceLocation]
        GuiHelper.openGui(gui, player)
      }
    }
    super.onItemRightClick(world, player, hand)
  }
}