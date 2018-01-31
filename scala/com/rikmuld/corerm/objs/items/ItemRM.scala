package com.rikmuld.corerm.objs.items

import com.rikmuld.corerm.objs.ObjDefinition

class ItemRM(modId:String, info: ObjDefinition) extends ItemSimple {
  def getItemInfo: ObjDefinition =
    info

  def getModId: String =
    modId
}