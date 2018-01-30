package com.rikmuld.corerm.objs.items

import com.rikmuld.corerm.objs.ObjDefinition
import com.rikmuld.corerm.objs.Properties.{FoodPoints, LikedByWolfs, Saturation}
import net.minecraft.item.ItemFood

class ItemFoodRM(modId:String, info: ObjDefinition, foodPoints: Int, saturation: Float, likedByWolfs: Boolean)
  extends ItemFood(foodPoints, saturation, likedByWolfs) with ItemSimple {

  def this(modId: String, info: ObjDefinition) = {
    this(modId, info, foodPoints, saturation, wolfMeat)

    def foodPoints =
      info.get(classOf[FoodPoints]).get.amount

    def saturation =
      info.get(classOf[Saturation]).get.kind.getSaturation(foodPoints)

    def wolfMeat =
      info.get(classOf[LikedByWolfs]).get.wolfMeat
  }

  def getItemInfo: ObjDefinition =
    info

  def getModId: String =
    modId
}