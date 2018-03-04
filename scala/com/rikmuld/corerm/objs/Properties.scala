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

  trait BlockProperty extends Property

  trait ItemProperty extends Property

  trait MetaDependable[A]

  trait StateDependable[A]

  case class Name(name: String) extends Property

  case class Tab(tab: CreativeTabs) extends ItemProperty

  case class PropMaterial(material: Material) extends BlockProperty

  case class Hardness(hardness: Float) extends BlockProperty //with StateDependable[Float]

  object Hardness {
    final val DIRT = 0.5f
    final val STONE = 1.5f
    final val WOOD = 2.0f
    final val IRON_ORE = 3.0f
    final val IRON_BLOCK = 5.0f
    final val OBSIDIAN = 50f
  }

  case class Resistance(resistance: Float) extends BlockProperty //with StateDependable[Float]

  object Resistance {
    final val STONE = 10f
    final val WOOD = 5f
    final val IRON_ORE = 5f
    final val IRON_BLOCK = 10f
    final val OBSIDIAN = 2000f
  }

  case class LightOpacity(opacity: Int) extends BlockProperty //with StateDependable[Int]

  object LightOpacity {
    final val NON_FULL_CUBE = 0
    final val WEB = 1
    final val WATER = 3
    final val FULL_CUBE = 255
  }

  case class LightLevel(light: Float) extends BlockProperty //with StateDependable[Float]

  object LightLevel {
    final val REDSTONE_TORCH = 0.5f
    final val PORTAL = 0.75f
    final val TORCH = 0.9375f
    final val LAVA = 1f
  }

  case class HarvestLevel(level: Int, tool: String) extends BlockProperty //with StateDependable[(Int, String)]

  object HarvestLevel {
    final val WOOD = 0
    final val STONE = 1
    final val IRON = 2
    final val DIAMOND = 3
  }

  case object Unstable extends BlockProperty

  case object TileModelOnly extends BlockProperty //with StateDependable[Boolean]

  case object NonCube extends BlockProperty //with StateDependable[Boolean]

  case object Invisible extends BlockProperty //with StateDependable[Boolean]

  case object Air extends BlockProperty //with StateDependable[Boolean]

  case object Ticker extends BlockProperty

  case object NoCollision extends BlockProperty //with StateDependable[Boolean]

  case class ItemClass[T <: ItemSimple](item: Class[T]) extends ItemProperty

  case class BlockClass[T <: BlockSimple](block: Class[T]) extends BlockProperty

  case class TileEntityClass[T <: TileEntitySimple](tile: Class[T]) extends BlockProperty //with StateDependable[Class[T]

  case class MaxDamage(damage: Int) extends ItemProperty

  case class MaxStackSize(size: Int) extends ItemProperty //with MetaDependable[Int]

  case class ItemMetaData(names: String*) extends ItemProperty

  case class ItemMetaFromState(property: String) extends ItemProperty

  case class BlockStates(states: States) extends BlockProperty

  case class FoodPoints(amount: Int) extends ItemProperty

  case class Saturation(kind: Saturation.Saturation) extends ItemProperty

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

  case class RenderType(layer: BlockRenderLayer) extends BlockProperty

  case class StepType(step: SoundType) extends BlockProperty

  case class ForceSubtypes(hasSubtypes: Boolean) extends ItemProperty

  case class LikedByWolfs(wolfMeat: Boolean) extends ItemProperty

  case class PropArmorMaterial(material: ArmorMaterial) extends ItemProperty

  case class ArmorType(typ: EntityEquipmentSlot) extends ItemProperty

  case class ArmorTexture(texture: String) extends ItemProperty

  case class GuiTrigger(id: ResourceLocation)
    extends ItemProperty
      with BlockProperty
      //with StateDependable[ResourceLocation]
      //with MetaDependable[ResourceLocation]

  case class GuiTriggerMeta(ids: (Int, ResourceLocation)*) extends ItemProperty //TODO remove once meta dependable is implemented

  case class ForMeta[A](defaultProperty: MetaDependable[A],
                        metaOverride: Int => Option[A]) extends ItemProperty

  case class ForState[A, S](state: String,
                            defaultProperty: StateDependable[A],
                            stateOverride: S => Option[A]) extends BlockProperty

  //add props for bounding box, item dropped and quantity dropped, the first two state dependable
  //add prop for bounds structure
}

