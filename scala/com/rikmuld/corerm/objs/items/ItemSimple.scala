package com.rikmuld.corerm.objs.items

import com.rikmuld.corerm.gui.GuiHelper
import com.rikmuld.corerm.objs.ObjDefinition
import com.rikmuld.corerm.objs.Properties._
import net.minecraft.client.renderer.block.model.{ModelBakery, ModelResourceLocation}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item._
import net.minecraft.util._
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

trait ItemSimple extends Item {
  getItemInfo.apply(this, getModId)

  val metadata: Option[ItemMetaData] =
    getItemInfo.get(classOf[ItemMetaData])

  private val simpleName: String =
    getItemInfo.get(classOf[Name]).get.name

  private val registryName =
    metadata.fold(getRegistryName)(meta =>
      new ResourceLocation(getModId, simpleName + "_" + meta(meta))
    )

  def getItemInfo: ObjDefinition

  def getModId: String

  override def getCreativeTabs: Array[CreativeTabs] =
    Option(getCreativeTab).fold(Array(CreativeTabs.SEARCH))(tab => Array(tab, CreativeTabs.SEARCH))

  override def getMetadata(damageValue: Int): Int =
    metadata.fold(0)(_ => damageValue)

  override def getUnlocalizedName(stack:ItemStack): String =
    "item." + getRegistryName(stack.getItemDamage).toString

  def getRegistryName(meta: Int): ResourceLocation =
    registryName(meta)

  override def getSubItems(tab:CreativeTabs, subItems:NonNullList[ItemStack]): Unit =
    if(getCreativeTabs.contains(tab))
      metadata.fold[Unit] {
        subItems.add(new ItemStack(this, 1, 0))
      }{ meta =>
        for(i <- meta.names.indices)
          subItems.add(new ItemStack(this, 1, i))
      }

  //TODO
  override def onItemRightClick(world: World, player: EntityPlayer, hand:EnumHand): ActionResult[ItemStack] = {
    if (hand == EnumHand.MAIN_HAND) {
      val item = player.getHeldItem(hand)
      val success = new ActionResult(EnumActionResult.SUCCESS, item)
      val fail = new ActionResult(EnumActionResult.PASS, item)

      if(getItemInfo.get(classOf[GuiTriggerMeta]).exists(
        _.ids.find(_._1 == item.getItemDamage).fold(false)(gui => {
          GuiHelper.openGui(gui._2, player)
          true
        })
      )) success

      else getItemInfo.get(classOf[GuiTrigger]).fold(fail)(trigger => {
        GuiHelper.openGui(trigger.id, player)
        success
      })
    } else super.onItemRightClick(world, player, hand)
  }

  @SideOnly(Side.CLIENT)
  def registerRenders(): Unit =
    metadata.fold {
      ModelLoader.setCustomModelResourceLocation(this, 0, modelResource(getRegistryName))
    } { meta =>
      for(i <- meta.names.indices){
        ModelBakery.registerItemVariants(this, registryName(i))
        ModelLoader.setCustomModelResourceLocation(this, i, modelResource(registryName(i)))
      }
    }

  private def modelResource(name: ResourceLocation): ModelResourceLocation =
    new ModelResourceLocation(name, "inventory")
}