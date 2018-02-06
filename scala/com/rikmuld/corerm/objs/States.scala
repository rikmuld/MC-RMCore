package com.rikmuld.corerm.objs

import com.rikmuld.corerm.objs.StateProperty.{StateProperty, _}
import com.rikmuld.corerm.utils.MathUtils
import net.minecraft.block.Block
import net.minecraft.block.properties.{IProperty, PropertyBool, PropertyDirection, PropertyInteger}
import net.minecraft.block.state.{BlockStateContainer, IBlockState}

import scala.collection.JavaConversions._

class States(props: StateProperty[_ <: Comparable[_]]*) {
  type Property = IProperty[_ <: Comparable[_]]

  val position: Map[StateProperty[_], Int] =
    props.tail.foldLeft[Seq[(StateProperty[_], Int)]](Seq(props.head -> 0)) {
      case (acc, next) =>
        acc :+ (next -> (acc.last._1.size + acc.last._2))
    }.toMap

  val properties: Seq[Property] = props.map({
    case PropInt(name, min, max, _) =>
      PropertyInteger.create(name, min, max)
    case PropBool(name, _) =>
      PropertyBool.create(name)
    case PropDirection(typ, name, _) =>
      PropertyDirection.create(name, typ.facings)
  })

  private def getProperty(name: String): Option[Property] =
    properties.find(_.getName == name)

  private def toProp[B <: Comparable[B]](prop: IProperty[B]): StateProperty[B] =
    props.find(_.name == prop.getName).get.asInstanceOf[StateProperty[B]]

  private def toMeta[A](prop: StateProperty[A], value: A): Int =
    prop.toInt(value) >> position(prop)

  def toMeta[B <: Comparable[B], A <: B](state: IBlockState): Int =
    state.getProperties.foldLeft(0) {
      case (meta, next) =>
        meta | toMeta(toProp(next._1.asInstanceOf[IProperty[B]]), next._2.asInstanceOf[A])
    }

  def createState(block: Block): BlockStateContainer =
    new BlockStateContainer(block, properties: _*)

  def getDefaultState[B <: Comparable[B], A <: B](state: BlockStateContainer): IBlockState =
    properties.zip(props.map(_.default)).foldLeft(state.getBaseState) {
      case (newState, next) =>
        newState.withProperty[B, A](next._1.asInstanceOf[IProperty[B]], next._2.asInstanceOf[A])
    }

  def fromMeta[B <: Comparable[B], A <: B](state: BlockStateContainer, meta: Int): IBlockState =
    properties.zip(props.map(prop =>
      prop.fromInt(MathUtils.bitGet(meta, position(prop), prop.size))
    )).foldLeft(state.getBaseState) {
      case (newState, next) =>
        newState.withProperty[B, A](next._1.asInstanceOf[IProperty[B]], next._2.asInstanceOf[A])
    }

  def get[B <: Comparable[B], A <: B](name: String, state: IBlockState): Option[A] =
    getProperty(name).map(prop => state.getValue(prop.asInstanceOf[IProperty[B]]).asInstanceOf[A])

  def is[B <: Comparable[B], A <: B](name: String, data: Any, state: IBlockState): Boolean =
    get[B, A](name, state).contains(data.asInstanceOf[A])

  def set[B <: Comparable[B], A <: B](name: String, data: Any, state: IBlockState): IBlockState =
    getProperty(name).fold(state)(prop => state.withProperty[B, A](prop.asInstanceOf[IProperty[B]], data.asInstanceOf[A]))

  def metaNames(name: String): Option[Seq[String]] =
    props.find(_.name  == name).map(_.metaNames)
}