package com.rikmuld.corerm.objs

import net.minecraft.item.ItemStack
import net.minecraftforge.common.AchievementPage
import net.minecraft.stats.Achievement
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

object RMAchievement {
  val achieveMap = new HashMap[String, ListBuffer[Achievement]]
  
  def addAchievement(mod:String, id:String, x:Int, y:Int, image:ItemStack, parent:Option[Achievement]):Achievement = {
    if(!achieveMap.contains(mod))achieveMap(mod) = new ListBuffer[Achievement]()
    val achieve = new Achievement("achievement." + id, id, x, y, image, if(parent.isDefined) parent.get else null)
    achieveMap(mod).append(achieve)
    achieve.registerStat
    achieve
  }
  def buildPage(mod:String, name:String) {
    val page = new AchievementPage(name, achieveMap(mod).toArray:_*)
    AchievementPage.registerAchievementPage(page)
  }
}