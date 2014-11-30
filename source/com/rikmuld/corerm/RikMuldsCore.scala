package com.rikmuld.corerm

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.SidedProxy
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.network.NetworkRegistry
import com.rikmuld.corerm.core.ProxyServer
import com.rikmuld.corerm.core.ModInfo
import com.rikmuld.corerm.core.ObjRegistry
import com.rikmuld.corerm.core.MiscRegistry

@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION, dependencies = ModInfo.MOD_DEPENDENCIES, modLanguage = ModInfo.MOD_LANUAGE)
object CampingMod {

  @SidedProxy(clientSide = ModInfo.MOD_CLIENT_PROXY, serverSide = ModInfo.MOD_SERVER_PROXY)
  var proxy: ProxyServer = null

  @EventHandler
  def Init(event: FMLInitializationEvent) {
    proxy.register
    MiscRegistry.init(event)
    ObjRegistry.init
  }
  @EventHandler
  def preInit(event: FMLPreInitializationEvent) {
    MiscRegistry.preInit(event)
    ObjRegistry.preInit
  }
  @EventHandler
  def PosInit(event: FMLPostInitializationEvent) {
    MiscRegistry.postInit(event)
    ObjRegistry.postInit
  }
}