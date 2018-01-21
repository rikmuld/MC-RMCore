package com.rikmuld.corerm

import net.minecraft.client.gui.Gui
import net.minecraftforge.fml.common.network.IGuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.SideOnly

import scala.collection.mutable.HashMap
import net.minecraft.inventory.Container
import net.minecraftforge.fml.relauncher.Side
import net.minecraft.inventory.IInventory
import net.minecraft.client.Minecraft
import com.rikmuld.corerm.objs.PropType
import com.rikmuld.corerm.objs.Properties._
import com.rikmuld.corerm.objs.ObjInfo
import net.minecraft.item.Item
import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader

class ProxyServer extends IGuiHandler {
  val guis = HashMap[Integer, Tuple2[Class[_], Class[_]]]();

  def getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Object = {
    try {
      (Option(guis(id)._1) map (gui => gui.getConstructor(classOf[EntityPlayer], classOf[IInventory]).newInstance(player, world.getTileEntity(new BlockPos(x, y, z))).asInstanceOf[Container])).getOrElse(null)
    } catch {
      case e: NoSuchMethodException => guis(id)._1.getConstructor(classOf[EntityPlayer]).newInstance(player).asInstanceOf[Container];
    }
  }
  @SideOnly(Side.CLIENT)
  def getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Object = {
    try {
      guis(id)._2.getConstructor(classOf[EntityPlayer], classOf[IInventory]).newInstance(player, world.getTileEntity(new BlockPos(x, y, z))).asInstanceOf[Gui];
    } catch {
      case e: NoSuchMethodException => guis(id)._2.getConstructor(classOf[EntityPlayer]).newInstance(player).asInstanceOf[Gui];
    }
  }
  def registerGui[T <: Container, TT <: Gui](container: Class[T], gui: Class[TT]):Int = {
    guis(guis.size) = (container, null)    
    guis.size-1
  }
  def registerRendersFor(event: ModelRegistryEvent, item: Item, info:ObjInfo, modId:String) {}
}

@SideOnly(Side.CLIENT)
class ProxyClient extends ProxyServer {
  override def registerGui[T <: Container, TT <: Gui](container: Class[T], gui: Class[TT]):Int = {
    guis(guis.size) = (container, gui)   
    guis.size-1
  }
  override def registerRendersFor(event: ModelRegistryEvent, item: Item, info:ObjInfo, modId:String){
    if(info.hasProp(PropType.METADATA)){
      val meta = info.getProp[Metadata](PropType.METADATA)
      for(i <- 0 until meta.names.length){
        ModelBakery.registerItemVariants(item, new ResourceLocation(modId, info.getValue[String](PropType.NAME) + "_" + meta.getName(i)))
        ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(new ResourceLocation(modId, info.getValue[String](PropType.NAME)) + "_" + meta.getName(i), "inventory"))
      }
    } else ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation(modId, info.getValue[String](PropType.NAME)), "inventory"))

    Minecraft.getMinecraft.getBlockRendererDispatcher
  }
}