package com.rikmuld.corerm.objs

import com.rikmuld.corerm.objs.StateProperty.DirectionType._
import com.rikmuld.corerm.utils.MathUtils
import net.minecraft.util.EnumFacing

object StateProperty {
  trait StateProperty[A] {
    val name: String

    val size: Int

    val default: A

    val metaNames: Seq[String]

    def toInt(a: A): Int

    def fromInt(int: Int): A
  }

  case class PropBool(name: String, default: java.lang.Boolean = false)
    extends StateProperty[java.lang.Boolean] {

    val size:Int =
      1

    val metaNames: Seq[String] =
      Seq(s"${name}_false", s"${name}_true")

    def toInt(a: java.lang.Boolean): Int =
      MathUtils.toInt(a)

    def fromInt(int: Int): java.lang.Boolean =
      int == 1
  }

  case class PropInt(name: String, min: Int, length: Int, default: java.lang.Integer = 0)
    extends StateProperty[java.lang.Integer] {

    val size:Int =
      MathUtils.bitsNeeded(length)

    val metaNames: Seq[String] =
      min until (min + length) map(name + "_" + _.toString)

    def toInt(a: java.lang.Integer): Int =
      a - min

    def fromInt(int: Int): java.lang.Integer =
      int + min
  }

  //default only for when the block is not placed by en entity, otherwise the facing of the entity is used as a default
  //changing the name prevents this
  case class PropDirection(typ: DirectionType, name: String = "facing", default:EnumFacing = EnumFacing.SOUTH)
    extends StateProperty[EnumFacing] {

    val size:Int =
      typ.size

    def toInt(a: EnumFacing): Int = typ match {
      case All =>
        a.getIndex
      case Horizontal =>
        a.getHorizontalIndex
    }

    val metaNames: Seq[String] = typ match {
      case All =>
        EnumFacing.VALUES.map(name + "_" + _.name)
      case Horizontal =>
        EnumFacing.HORIZONTALS.map(name + "_" + _.name)
    }

    def fromInt(int: Int): EnumFacing = typ match {
      case All =>
        EnumFacing.getFront(int)
      case Horizontal =>
        EnumFacing.getHorizontal(int)
    }
  }

  object DirectionType {
    trait DirectionType {
      val size: Int = this match {
        case All => 3
        case Horizontal => 2
      }

      val facings: Seq[EnumFacing] = this match {
        case All => EnumFacing.VALUES
        case Horizontal => EnumFacing.HORIZONTALS
      }
    }

    case object All extends DirectionType
    case object Horizontal extends DirectionType
  }

  case class PropEnum[A <: Enum[A]](name: String, default: Enum[A]) extends StateProperty[Enum[A]] {
    val size: Int =
      MathUtils.bitsNeeded(classOf[Enum[A]].getEnumConstants.length)

    val metaNames: Seq[String] =
      classOf[Enum[A]].getEnumConstants.map(_.name.toLowerCase)

    def toInt(enum: Enum[A]): Int =
      enum.ordinal

    def fromInt(int: Int): Enum[A] =
      classOf[Enum[A]].getEnumConstants.apply(int)
  }
}
