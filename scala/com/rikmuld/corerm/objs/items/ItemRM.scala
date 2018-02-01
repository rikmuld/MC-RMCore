package com.rikmuld.corerm.objs.items

import com.rikmuld.corerm.objs.ObjDefinition
import net.minecraft.item.Item

class ItemRM(modId:String, info: ObjDefinition) extends Item with ItemSimple {
  def getItemInfo: ObjDefinition =
    info

  def getModId: String =
    modId
}