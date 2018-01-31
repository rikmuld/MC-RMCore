package com.rikmuld.corerm.objs

import com.rikmuld.corerm.objs.Properties._
import com.rikmuld.corerm.objs.blocks.BlockSimple
import com.rikmuld.corerm.objs.items._
import net.minecraft.block.Block
import net.minecraft.item.Item

class ObjDefinition(props: Property*) {
  val properties: Set[Property] =
    props.toSet

  def contains[A <: Property](property: Class[A]): Boolean =
    properties.exists(p => p.getClass == property)

  def get[A <: Property](property: Class[A]): Option[A] =
    properties.find(_.getClass == property).map(_.asInstanceOf[A])

  def has[A <: Property](property: Class[A]): Boolean =
    properties.exists(_.getClass == property)

  def has(property: Property): Boolean =
    properties.contains(property)

  def apply(block: Block, modId: String): Unit =
    properties.foreach(prop => ObjDefinition.apply(block, prop, modId))

  def apply(item: Item, modId: String): Unit =
    properties.foreach(prop => ObjDefinition.apply(item, prop, modId))

  def update(property: Property): ObjDefinition =
    new ObjDefinition((properties + property).toSeq:_*)

  def update(props: Property*): ObjDefinition =
    new ObjDefinition((properties ++ props).toSeq:_*)

  def createItem(modId: String): ItemSimple =
    ObjDefinition.instantiateItem(this, modId)

  def createItemBlock(modId: String, block: Block): ItemSimple =
    ObjDefinition.instantiateItemBlock(this, modId, block)

  def createBlock(modId: String): BlockSimple =
    ObjDefinition.instantiateBlock(this, modId)

  def create(modId: String): (BlockSimple, ItemSimple) =
    ObjDefinition.instantiateAll(this, modId)
}

object ObjDefinition {
  def apply(block: Block, prop: Property, modId: String): Unit = prop match {
    case Name(name) =>
      block.setRegistryName(modId, name)
      block.setUnlocalizedName(block.getRegistryName.toString)
    case Hardness(hardness) =>
      block.setHardness(hardness)
    case Resistance(resistance) =>
      block.setResistance(resistance)
    case LightOpacity(opacity) =>
      block.setLightOpacity(opacity)
    case LightLevel(light) =>
      block.setLightLevel(light)
    case HarvestLevel(level, tool) =>
      block.setHarvestLevel(tool, level)
    case _ =>
  }

  def apply(item: Item, prop: Property, modId: String): Unit = prop match {
    case Name(name) =>
      item.setRegistryName(modId, name)
      item.setUnlocalizedName(item.getRegistryName.toString)
    case Tab(tab) =>
      item.setCreativeTab(tab)
    case MaxStackSize(size) =>
      item.setMaxStackSize(size)
    case MaxDamage(damage) =>
      item.setMaxDamage(damage)
    case ItemMetaData(_) =>
      item.setHasSubtypes(true)
    case _ =>
  }

  def instantiateItem(definition: ObjDefinition, modId: String): ItemSimple =
    definition.get(classOf[ItemClass[_ <: ItemSimple]]).fold(
      if(definition.contains(classOf[FoodPoints]) || definition.contains(classOf[Saturation]))
        new ItemFoodRM(modId, definition)
      else if(definition.contains(classOf[ArmorType]))
        new ItemArmorRM(modId, definition)
      else
        new ItemRM(modId, definition)
    )(_.item.getConstructor(
      classOf[String],
      classOf[ObjDefinition]
    ).newInstance(modId, definition))

  def instantiateItemBlock(definition: ObjDefinition, modId: String, block: Block): ItemBlockRM =
    definition.get(classOf[ItemClass[_ <: ItemBlockRM]]).fold {
      classOf[ItemBlockRM].getConstructor(
        classOf[String],
        classOf[ObjDefinition],
        classOf[Block]
      ).newInstance(modId, definition, block)
    } { itemClass =>
      itemClass.item.getConstructor(classOf[Block]).newInstance(block)
    }

  def instantiateBlock(definition: ObjDefinition, modId: String): BlockSimple =
    definition.get(classOf[BlockClass[_ <: BlockSimple]]).map(_.block).getOrElse(classOf[BlockSimple]).getConstructor(
      classOf[String],
      classOf[ObjDefinition]
    ).newInstance(modId, definition)

  def instantiateAll(definition: ObjDefinition, modId: String): (BlockSimple, ItemBlockRM) = {
    val block = instantiateBlock(definition, modId)
    (block, instantiateItemBlock(definition, modId, block))
  }
}