package com.rikmuld.corerm

import com.rikmuld.corerm.RMMod._
import com.rikmuld.corerm.features.bounds.{BoundsData, TileBounds}
import com.rikmuld.corerm.features.tabbed.TabSwitch
import com.rikmuld.corerm.network._
import com.rikmuld.corerm.tileentity.RMTile
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side

@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, dependencies = MOD_DEPENDENCIES, modLanguage = MOD_LANUAGE)
object RMMod {
  final val MOD_ID = "corerm"
  final val MOD_NAME = "RikMuld's Core"
  final val MOD_VERSION = "1.2f"
  final val MOD_LANUAGE = "scala"
  final val MOD_DEPENDENCIES = "required-after:forge@[v13.20.1.2386,)"
  final val PACKET_CHANEL = MOD_ID
  final val MOD_SERVER_PROXY = "com.rikmuld.corerm.ProxyServer"
  final val MOD_CLIENT_PROXY = "com.rikmuld.corerm.ProxyClient"

  @SidedProxy(clientSide = MOD_CLIENT_PROXY, serverSide = MOD_SERVER_PROXY)
  var proxy: ProxyServer = null
  var network: SimpleNetworkWrapper = _

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) {
    network = NetworkRegistry.INSTANCE.newSimpleChannel(PACKET_CHANEL)
    network.registerMessage(classOf[Handler], classOf[PacketGlobal], 0, Side.SERVER)
    network.registerMessage(classOf[Handler], classOf[PacketGlobal], 0, Side.CLIENT)
  }
  @EventHandler
  def Init(event: FMLInitializationEvent) {
    registerPacket(classOf[TileData].asInstanceOf[Class[BasicPacketData]])
    registerPacket(classOf[TabSwitch].asInstanceOf[Class[BasicPacketData]])
    registerPacket(classOf[TabSwitch].asInstanceOf[Class[BoundsData]])
    
    GameRegistry.registerTileEntity(classOf[RMTile], MOD_ID + "_coreTile")
    GameRegistry.registerTileEntity(classOf[TileBounds], MOD_ID + "_boundsTile")
    
    NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy)
  }
  @EventHandler
  def PosInit(event: FMLPostInitializationEvent) {}  
  def registerPacket[T <: BasicPacketData](packet:Class[T]){
    PacketDataManager.registerPacketData(packet.asInstanceOf[Class[BasicPacketData]])
  }
}

object Lib {
  object TextInfo {
    final val COLOURS_DYE = Array("0", "4", "2", "6", "1", "5", "3", "7", "8", "d", "a", "e", "9", "d", "6", "f")
  
    final val COLOR_BLACK = "\u00a70"
    final val COLOR_BLUE_DARK = "\u00a71"
    final val COLOR_GREEN_DARK = "\u00a72"
    final val COLOR_AQUA_DARK = "\u00a73"
    final val COLOR_RED_DARK = "\u00a74"
    final val COLOR_PURPLE = "\u00a75"
    final val COLOR_ORANGE = "\u00a76"
    final val COLOR_GRAY = "\u00a77"
    final val COLOR_GRAY_DARK = "\u00a78"
    final val COLOR_BLUE = "\u00a79"
    final val COLOR_GREEN_LIGHT = "\u00a7a"
    final val COLOR_AQUA_LIGHT = "\u00a7b"
    final val COLOR_RED = "\u00a7c"
    final val COLOR_PINK = "\u00a7d"
    final val COLOR_YELLOW = "\u00a7e"
    final val COLOR_WHITE = "\u00a7f"
  
    final val FORMAT_OBFUSCATED = "\u00a7k"
    final val FORMAT_BOLD = "\u00a7l"
    final val FORMAT_THROUGH = "\u00a7m"
    final val FORMAT_UNDERLINE = "\u00a7n"
    final val FORMAT_ITALIC = "\u00a7o"
  
    final val RESET = "\u00a7r"
  }
  
  object TextureInfo {
    final val GUI_LOCATION = MOD_ID + ":textures/gui/"
    final val GUI_TAB_UTILS = GUI_LOCATION + "gui_tab_utils.png"
  }
}