package com.rikmuld.corerm.objs

import com.rikmuld.corerm.objs.StateProperty.DirectionType._
import com.rikmuld.corerm.utils.MathUtils
import net.minecraft.util.EnumFacing

object StateProperty {
  trait StateProperty[A] {
    val name: String

    val size: Int = this match {
      case PropBool(_, _) =>
        1
      case PropInt(_, min, max, _) =>
        MathUtils.bitsNeeded(max - min)
      case PropDirection(typ, _, _) =>
        typ.size
    }

    val default: A

    def toInt(a: A): Int

    def fromInt(int: Int): A
  }

  case class PropBool(name: String, default: Boolean = false)
    extends StateProperty[Boolean] {

    def toInt(a: Boolean): Int =
      MathUtils.toInt(a)

    def fromInt(int: Int): Boolean =
      int == 1
  }

  case class PropInt(name: String, min: Int, max: Int, default: Int = 0)
    extends StateProperty[Int] {

    def toInt(a: Int): Int =
      a

    def fromInt(int: Int): Int =
      int
  }

  //default only for when the block is not placed by en entity, otherwise the facing of the entity is used as a default
  case class PropDirection(typ: DirectionType, name: String = "facing", default:EnumFacing = EnumFacing.SOUTH)
    extends StateProperty[EnumFacing] {

    def toInt(a: EnumFacing): Int = typ match {
      case All =>
        a.getIndex
      case Horizontal =>
        a.getHorizontalIndex
      case Split =>
        a.getHorizontalIndex % 2
    }

    def fromInt(int: Int): EnumFacing = typ match {
      case All =>
        EnumFacing.getFront(int)
      case Horizontal | Split =>
        EnumFacing.getHorizontal(int)
    }
  }

  object DirectionType {
    trait DirectionType {
      val size: Int = this match {
        case All => 3
        case Horizontal => 2
        case Split => 1
      }

      val facings: Seq[EnumFacing] = this match {
        case All => EnumFacing.VALUES
        case Horizontal => EnumFacing.HORIZONTALS
        case Split => Seq(EnumFacing.SOUTH, EnumFacing.WEST)
      }
    }

    case object All extends DirectionType
    case object Horizontal extends DirectionType
    case object Split extends DirectionType
  }
}
