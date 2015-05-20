package com.rikmuld.corerm

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side
import com.rikmuld.corerm.network.PacketGlobal
import com.rikmuld.corerm.network.BasicPacketData
import com.rikmuld.corerm.network.PacketDataManager
import com.rikmuld.corerm.network.Handler
import com.rikmuld.corerm.RMMod._
import com.rikmuld.corerm.network.TileData
import com.rikmuld.corerm.objs.RMTileWithRot
import com.rikmuld.corerm.objs.RMTile
import com.rikmuld.corerm.network.Handler
import com.rikmuld.corerm.network.PacketGlobal
import com.rikmuld.corerm.network.TileData
import com.rikmuld.corerm.objs.RMTile
import com.rikmuld.corerm.objs.RMTileWithRot

@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, dependencies = MOD_DEPENDENCIES, modLanguage = MOD_LANUAGE)
object RMMod {
  final val MOD_ID = "corerm"
  final val MOD_NAME = "RikMuld's Core"
  final val MOD_VERSION = "1.1"
  final val MOD_LANUAGE = "scala"
  final val MOD_DEPENDENCIES = "required-after:Forge@[v11.14.1.1397,)"
  final val PACKET_CHANEL = MOD_ID
  final val MOD_SERVER_PROXY = "com.rikmuld.corerm.ProxyServer"
  final val MOD_CLIENT_PROXY = "com.rikmuld.corerm.ProxyClient"

  @SidedProxy(clientSide = MOD_CLIENT_PROXY, serverSide = MOD_SERVER_PROXY)
  var proxy: ProxyServer = null
  
  @EventHandler
  def preInit(event: FMLPreInitializationEvent) {
    Objs.network = NetworkRegistry.INSTANCE.newSimpleChannel(PACKET_CHANEL)
    Objs.network.registerMessage(classOf[Handler], classOf[PacketGlobal], 0, Side.SERVER)
    Objs.network.registerMessage(classOf[Handler], classOf[PacketGlobal], 0, Side.CLIENT)
  }
  @EventHandler
  def Init(event: FMLInitializationEvent) {
    PacketDataManager.registerPacketData(classOf[TileData].asInstanceOf[Class[BasicPacketData]])
    
    GameRegistry.registerTileEntity(classOf[RMTile], MOD_ID + "_coreTile")
    GameRegistry.registerTileEntity(classOf[RMTileWithRot], MOD_ID + "_withRotation")
    
    NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy)
  }
  @EventHandler
  def PosInit(event: FMLPostInitializationEvent) {}
  
  object Objs {
    var network: SimpleNetworkWrapper = _
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
}