package com.rikmuld.corerm.objs

import com.rikmuld.corerm.objs.blocks.BlockSimple
import com.rikmuld.corerm.objs.items.ItemSimple
import com.rikmuld.corerm.tileentity.TileEntitySimple
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemArmor.ArmorMaterial
import net.minecraft.util.{BlockRenderLayer, ResourceLocation}

object Properties {
  trait Property

  case class Name(name: String) extends Property

  case class Tab(tab: CreativeTabs) extends Property

  case class PropMaterial(material: Material) extends Property

  case class Hardness(hardness: Float) extends Property

  object Hardness {
    final val DIRT = 0.5f
    final val STONE = 1.5f
    final val WOOD = 2.0f
    final val IRON_ORE = 3.0f
    final val IRON_BLOCK = 5.0f
    final val OBSIDIAN = 50f
  }

  case class Resistance(resistance: Float) extends Property

  object Resistance {
    final val STONE = 10f
    final val WOOD = 5f
    final val IRON_ORE = 5f
    final val IRON_BLOCK = 10f
    final val OBSIDIAN = 2000f
  }

  case class LightOpacity(opacity: Int) extends Property

  object LightOpacity {
    final val NON_FULL_CUBE = 0
    final val WEB = 1
    final val WATER = 3
    final val FULL_CUBE = 255
  }

  case class LightLevel(light: Float) extends Property

  object LightLevel {
    final val REDSTONE_TORCH = 0.5f
    final val PORTAL = 0.75f
    final val TORCH = 0.9375f
    final val LAVA = 1f
  }

  case class HarvestLevel(level: Int, tool: String) extends Property

  object HarvestLevel {
    final val WOOD = 0
    final val STONE = 1
    final val IRON = 2
    final val DIAMOND = 3
  }

  case object Unstable extends Property

  case object HasTileModel extends Property

  case object NonCube extends Property

  case object Invisible extends Property

  case class ItemClass[T <: ItemSimple](item: Class[T]) extends Property

  case class BlockClass[T <: BlockSimple](block: Class[T]) extends Property

  case class TileEntityClass[T <: TileEntitySimple](tile: Class[T]) extends Property

  case class MaxDamage(damage: Int) extends Property

  case class MaxStackSize(size: Int) extends Property

  case class ItemMetaData(names: String*) extends Property

  case class ItemMetaFromState(property: String) extends Property

  case class BlockStates(states: States) extends Property

  case class FoodPoints(amount: Int) extends Property

  case class Saturation(kind: Saturation.Saturation) extends Property

  object Saturation {
    trait Saturation {
      def getSaturation(foodPoints: Int): Float = this match {
        case Value(level) => level
        case Nourishment(value) => foodPoints * value
      }
    }

    case class Value(level: Float) extends Saturation
    case class Nourishment(value: Float) extends Saturation

    final val SuperNatural = Nourishment(2.4f)
    final val Good = Nourishment(1.6f)
    final val Normal = Nourishment(1.2f)
    final val Low = Nourishment(0.6f)
    final val Poor = Nourishment(0.2f)
  }

  case class RenderType(layer: BlockRenderLayer) extends Property

  case class StepType(step: SoundType) extends Property

  case class ForceSubtypes(hasSubtypes: Boolean) extends Property

  case class LikedByWolfs(wolfMeat: Boolean) extends Property

  case class PropArmorMaterial(material: ArmorMaterial) extends Property

  case class ArmorType(typ: EntityEquipmentSlot) extends Property

  case class ArmorTexture(texture: String) extends Property

  case class GuiTrigger(id: ResourceLocation) extends Property

  case class GuiTriggerMeta(ids: (Int, ResourceLocation)*) extends Property
}
