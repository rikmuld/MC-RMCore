package com.rikmuld.corerm.core

import com.rikmuld.corerm.misc.CustomModel
import com.rikmuld.corerm.misc.CustomModelLoader
import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.client.model.techne.TechneModel
import net.minecraftforge.client.model.techne.TechneModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.EnumHelper
import net.minecraft.item.ItemArmor.ArmorMaterial
import cpw.mods.fml.client.registry.RenderingRegistry
import net.minecraft.entity.Entity
import cpw.mods.fml.common.registry.EntityRegistry
import net.minecraft.entity.EntityList
import net.minecraft.entity.EntityList.EntityEggInfo
import scala.collection.JavaConversions._
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.common.BiomeDictionary.Type
import net.minecraft.potion.Potion
import net.minecraft.util.DamageSource
import net.minecraft.entity.EnumCreatureType
import net.minecraftforge.common.config.Configuration
import cpw.mods.fml.relauncher.Side
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.item.ItemFood
import net.minecraft.client.model.ModelWolf
import net.minecraft.client.model.ModelBiped
import com.rikmuld.corerm.common.network.TileData
import com.rikmuld.corerm.common.network.PacketGlobal
import com.rikmuld.corerm.common.network.BasicPacketData
import com.rikmuld.corerm.common.objs.tile.TileEntityWithRotation
import com.rikmuld.corerm.common.network.PacketDataManager
import com.rikmuld.corerm.common.network.Handler

object CoreObjs {
  var network: SimpleNetworkWrapper = _
  var modelLoader: TechneModelLoader = _
  var modelLoaderC: CustomModelLoader = _
}

object MiscRegistry {
  def init(event: FMLInitializationEvent) {
    PacketDataManager.registerPacketData(classOf[TileData].asInstanceOf[Class[BasicPacketData]])
    GameRegistry.registerTileEntity(classOf[TileEntityWithRotation], ModInfo.MOD_ID + "_withRotation")
  }
  def preInit(event: FMLPreInitializationEvent) {
    CoreObjs.modelLoader = new TechneModelLoader()
    CoreObjs.modelLoaderC = new CustomModelLoader()
    CoreObjs.network = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.PACKET_CHANEL)
    CoreObjs.network.registerMessage(classOf[Handler], classOf[PacketGlobal], 0, Side.SERVER)
    CoreObjs.network.registerMessage(classOf[Handler], classOf[PacketGlobal], 0, Side.CLIENT)
  }
  def postInit(event: FMLPostInitializationEvent) {}
  @SideOnly(Side.CLIENT)
  def initClient {
    
  }
  def initServer {
    
  }
}

object ObjRegistry {
  def preInit {

  }
  def init {
   
  }
  def postInit {
    
  }
  def register(block: Block, name: String) = GameRegistry.registerBlock(block, name)
  def register(item: Item, name: String) = GameRegistry.registerItem(item, name)
  def register(block: Block, name: String, itemBlock: Class[ItemBlock]) = GameRegistry.registerBlock(block, itemBlock, name)
}