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

  val metadata: Option[Seq[String]] = getItemInfo.get(classOf[ItemMetaData]) match {
    case None =>
      for {
        metaInfo <- getItemInfo.get(classOf[ItemMetaFromState])
        state <- getItemInfo.get(classOf[BlockStates])
      } yield {
        state.states.metaNames(metaInfo.property).get
      }
    case Some(names) =>
      Some(names.names)
  }


  metadata.foreach(_ => this.setHasSubtypes(true))

  getItemInfo.get(classOf[ForceSubtypes]).map(_.hasSubtypes).foreach(types =>
    this.setHasSubtypes(types)
  )

  private val simpleName: String =
    getItemInfo.get(classOf[Name]).get.name

  private val metaRegistryName =
    metadata.map(_.map(meta => new ResourceLocation(getModId, simpleName + "_" + meta)))

  def getItemInfo: ObjDefinition

  def getModId: String

  override def getCreativeTabs: Array[CreativeTabs] =
    Option(getCreativeTab).fold[Array[CreativeTabs]](Array())(tab =>
      Array(tab, CreativeTabs.SEARCH)
    )

  override def getMetadata(damageValue: Int): Int =
    metadata.fold(0)(_ => damageValue)

  override def getUnlocalizedName(stack:ItemStack): String =
    "item." + getRegistryName(stack.getItemDamage).toString

  def getRegistryName(meta: Int): ResourceLocation =
    metaRegistryName.fold(getRegistryName())(_(meta))

  override def getSubItems(tab:CreativeTabs, subItems:NonNullList[ItemStack]): Unit =
    if(getCreativeTabs.contains(tab))
      metadata.fold[Unit] {
        subItems.add(new ItemStack(this, 1, 0))
      }{ meta =>
        for(i <- meta.indices)
          subItems.add(new ItemStack(this, 1, i))
      }

  //TODO
  override def onItemRightClick(world: World, player: EntityPlayer, hand:EnumHand): ActionResult[ItemStack] = {
    if (hand == EnumHand.MAIN_HAND) {
      val item = player.getHeldItem(hand)
      val success = new ActionResult(EnumActionResult.SUCCESS, item)

      if(getItemInfo.get(classOf[GuiTriggerMeta]).exists(
        _.ids.find(_._1 == item.getItemDamage).fold(false)(gui => {
          GuiHelper.openGui(gui._2, player)
          true
        })
      )) success
      else if(getItemInfo.get(classOf[GuiTrigger]).exists(
        trigger => {
          GuiHelper.openGui(trigger.id, player)
          true
        }
      )) success
      else super.onItemRightClick(world, player, hand)
    } else super.onItemRightClick(world, player, hand)
  }

  @SideOnly(Side.CLIENT)
  def registerRenders(): Unit =
    metaRegistryName.fold {
      ModelLoader.setCustomModelResourceLocation(this, 0, modelResource(getRegistryName))
    } { names =>
      for(i <- names.indices){
        ModelBakery.registerItemVariants(this, names(i))
        ModelLoader.setCustomModelResourceLocation(this, i, modelResource(names(i)))
      }
    }

  private def modelResource(name: ResourceLocation): ModelResourceLocation =
    new ModelResourceLocation(name, "inventory")
}