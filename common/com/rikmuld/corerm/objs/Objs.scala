package com.rikmuld.corerm.objs

import net.minecraft.creativetab.CreativeTabs
import scala.collection.mutable.HashSet
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraft.item.ItemBlock
import com.rikmuld.corerm.objs.Properties._
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemArmor.ArmorMaterial
import net.minecraft.util.ResourceLocation
import com.rikmuld.corerm.RMMod
import scala.collection.mutable.ListBuffer

object PropType extends Enumeration {
  type PropType = Value
  val NAME,TAB,MATERIAL,HARDNESS,RECISTANCE,SOUND,OPACITY,LIGHT,HARVEST,
      ITEMBLOCK,MAX_DAMAGE,MAX_STACKSIZE,METADATA,HEAL,SATURATION,WOLFMEAT,
      ARMORMAT,ARMORTYPE,ARMORTEX,FORCESUBTYPE,GUITRIGGER,GUITRIGGER_META = Value
}

class ObjInfo(props:Prop*) {  
  def getValue[T](propType:PropType.PropType):T = props.filter { p => p.getType.equals(propType) }(0).value.asInstanceOf[T]
  def getProp[T <: Prop](propType:PropType.PropType):T = props.filter { p => p.getType.equals(propType) }(0).asInstanceOf[T]
  def hasProp(propType:PropType.PropType) = props.filter { p => p.getType.equals(propType) }.size>0
  def apply(block:Block) = props.foreach { prop => applyProp(block, prop) }
  def apply(item:Item) = props.foreach { prop => applyProp(item, prop) }
  def applyProp(block:Block, prop:Prop) = prop.getType match {
    case PropType.NAME => block.setUnlocalizedName(prop.value.asInstanceOf[String])
    case PropType.TAB => block.setCreativeTab(prop.value.asInstanceOf[CreativeTabs])
    case PropType.HARDNESS => block.setHardness(prop.value.asInstanceOf[Float])
    case PropType.RECISTANCE => block.setResistance(prop.value.asInstanceOf[Float])
    case PropType.OPACITY => block.setLightOpacity(prop.value.asInstanceOf[Int])
    case PropType.LIGHT => block.setLightLevel(prop.value.asInstanceOf[Float])
    case PropType.HARVEST => 
      val value = prop.value.asInstanceOf[Array[String]]
      block.setHarvestLevel(value(0), value(1).toInt)
    case _ =>
  }
  def applyProp(item:Item, prop:Prop) = prop.getType match {
    case PropType.NAME => item.setUnlocalizedName(prop.value.asInstanceOf[String])
    case PropType.TAB => item.setCreativeTab(prop.value.asInstanceOf[CreativeTabs])
    case PropType.MAX_DAMAGE => item.setMaxDamage(prop.value.asInstanceOf[Int])
    case PropType.MAX_STACKSIZE => item.setMaxStackSize(prop.value.asInstanceOf[Int])
    case _ =>
  }
  def register(block:RMCoreBlock, modId:String) {
    if(hasProp(PropType.ITEMBLOCK)) GameRegistry.registerBlock(block, getValue[Class[ItemBlock]](PropType.ITEMBLOCK), getValue[String](PropType.NAME))
    else GameRegistry.registerBlock(block, getValue[String](PropType.NAME))
    RMMod.proxy.registerRendersFor(Left(block), this, modId)
  }
  def register(item:RMCoreItem, modId:String){
    GameRegistry.registerItem(item, getValue[String](PropType.NAME))
    RMMod.proxy.registerRendersFor(Right(item), this, modId)
  }
}

object Properties {
  abstract class Prop(val value:Any) {
    def getType:PropType.PropType
  }
  case class Name(name:String) extends Prop(name) {
    override def getType:PropType.PropType = PropType.NAME
  }
  case class Tab(tab:CreativeTabs) extends Prop(tab){
    override def getType:PropType.PropType = PropType.TAB
  }
  case class Materia(material:Material) extends Prop(material){
    override def getType:PropType.PropType = PropType.MATERIAL
  }
  case class Hardness(hardness:Float) extends Prop(hardness){
    override def getType:PropType.PropType = PropType.HARDNESS
  }
  case class Recistance(resistance:Float) extends Prop(resistance){
    override def getType:PropType.PropType = PropType.RECISTANCE
  }
  case class LightOpacity(opacity:Int) extends Prop(opacity){
    override def getType:PropType.PropType = PropType.OPACITY
  }
  case class LightLevel(light:Float) extends Prop(light){
    override def getType:PropType.PropType = PropType.LIGHT
  }
  case class HarvestLevel(tool:String, level:Int) extends Prop(Array[String](tool, level.toString)){
    override def getType:PropType.PropType = PropType.HARVEST
  }
  case class ItemBl[T <: ItemBlock](item:Class[T]) extends Prop(item){
    override def getType:PropType.PropType = PropType.ITEMBLOCK
  }
  case class MaxDamage(damage:Int) extends Prop(damage){
    override def getType:PropType.PropType = PropType.MAX_DAMAGE
  }
  case class MaxStacksize(size:Int) extends Prop(size){
    override def getType:PropType.PropType = PropType.MAX_STACKSIZE
  }
  case class Metadata(names:String*) extends Prop(names.toArray){
    override def getType:PropType.PropType = PropType.METADATA
    def getName(meta:Int) = names(meta)
  }
  case class Heal(amount:Int) extends Prop(amount){
    override def getType:PropType.PropType = PropType.HEAL
  }
  case class Saturation(amount:Float) extends Prop(amount){
    override def getType:PropType.PropType = PropType.SATURATION
  }
  case class WolfMeat(wolfMeat:Boolean) extends Prop(wolfMeat){
    override def getType:PropType.PropType = PropType.WOLFMEAT
  }
  case class ArmorMateria(material:ArmorMaterial) extends Prop(material){
    override def getType:PropType.PropType = PropType.ARMORMAT
  }
  case class ArmorTyp(typ:Int) extends Prop(typ){
    override def getType:PropType.PropType = PropType.ARMORTYPE
  }
  case class ArmorTexture(texMain:String) extends Prop(texMain) {
    override def getType:PropType.PropType = PropType.ARMORTEX
  }
  case class ForceSubtype(flag:Boolean) extends Prop(flag) {
    override def getType:PropType.PropType = PropType.FORCESUBTYPE
  }
  case class GuiTrigger(id:Int) extends Prop(id) {
    override def getType:PropType.PropType = PropType.GUITRIGGER
  }
  case class GuiTriggerMeta(ids:(Int, Int)*) extends Prop(ids.toArray) {
    override def getType:PropType.PropType = PropType.GUITRIGGER_META
  }
}