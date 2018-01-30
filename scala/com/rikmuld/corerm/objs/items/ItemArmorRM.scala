package com.rikmuld.corerm.objs.items

import com.rikmuld.corerm.objs.ObjDefinition
import com.rikmuld.corerm.objs.Properties.{ArmorTexture, ArmorType, PropArmorMaterial}
import net.minecraft.entity.Entity
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.{ItemArmor, ItemStack}

class ItemArmorRM(modId:String, info: ObjDefinition)
  extends ItemArmor(
    info.get(classOf[PropArmorMaterial]).get.material, 0,
    info.get(classOf[ArmorType]).get.typ
  ) with ItemSimple {

  def getItemInfo: ObjDefinition =
    info

  def getModId: String =
    modId

  override def getArmorTexture(stack: ItemStack, entity: Entity, slot: EntityEquipmentSlot, layer: String): String =
    getItemInfo.get(classOf[ArmorTexture]).get.texture
}