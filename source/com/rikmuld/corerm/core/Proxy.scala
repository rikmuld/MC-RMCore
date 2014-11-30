package com.rikmuld.corerm.core

import cpw.mods.fml.relauncher.SideOnly
import cpw.mods.fml.relauncher.Side

class ProxyServer {
  def register = MiscRegistry.initServer
}

@SideOnly(Side.CLIENT)
class ProxyClient extends ProxyServer {
  override def register = MiscRegistry.initClient
}