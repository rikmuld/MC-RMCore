package com.rikmuld.corerm.objs

import com.rikmuld.corerm.objs.StateProperty.{StateProperty, _}
import com.rikmuld.corerm.utils.MathUtils
import net.minecraft.block.Block
import net.minecraft.block.properties.{IProperty, PropertyBool, PropertyDirection, PropertyInteger}
import net.minecraft.block.state.{BlockStateContainer, IBlockState}

import scala.collection.JavaConversions._

class States(props: StateProperty[_]*) {
  type Property = IProperty[_ <: Comparable[_]]

  val position: Map[StateProperty[_], Int] = props.tail.foldLeft(Map(props.head -> 0)) {
    case (acc, next) =>
      acc ++ Map(next -> (acc.last._1.size + acc.last._2))
  }

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

  private def toProp[A](prop: IProperty[A]): StateProperty[A] =
    props.find(_.name == prop.getName).get.asInstanceOf[StateProperty[A]]

  private def toMeta[A](prop: StateProperty[A], value: A): Int =
    prop.toInt(value) >> position(prop)

  def toMeta(state: IBlockState): Int =
    state.getProperties.foldLeft(0) {
      case (meta, next) =>
        meta | toMeta(toProp(next._1), next._2)
    }

  def createState(block: Block): BlockStateContainer =
    new BlockStateContainer(block, properties: _*)

  def getDefaultState(state: BlockStateContainer): IBlockState =
    properties.zip(props.map(_.default)).foldLeft(state.getBaseState) {
      case (newState, next) =>
        newState.withProperty(next._1, next._2)
    }

  def fromMeta(state: BlockStateContainer, meta: Int): IBlockState =
    properties.zip(props.map(prop =>
      prop.fromInt(MathUtils.bitGet(meta, position(prop), prop.size))
    )).foldLeft(state.getBaseState) {
      case (newState, next) =>
        newState.withProperty(next._1, next._2)
    }

  def get[A](name: String, state: IBlockState): Option[A] =
    getProperty(name).map(prop => state.getValue(prop).asInstanceOf[A])

  def is[A](name: String, data: A, state: IBlockState): Boolean =
    get[A](name, state).contains(data)

  def set[A](name: String, data: A, state: IBlockState): IBlockState =
    getProperty(name).fold(state)(prop => state.withProperty(prop, data))
}