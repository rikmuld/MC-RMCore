package com.rikmuld.corerm.objs.blocks

import com.rikmuld.corerm.objs.ObjDefinition
import com.rikmuld.corerm.objs.Properties.PropMaterial
import net.minecraft.block.Block

class BlockRM(modId:String, info:ObjDefinition)
  extends Block(info.get(classOf[PropMaterial]).get.material) with BlockSimple {

  override def getModId: String =
    modId

  override def getInfo: ObjDefinition =
    info
}
