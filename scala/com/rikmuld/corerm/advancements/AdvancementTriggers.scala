package com.rikmuld.corerm.advancements

import java.lang.reflect.Method

import net.minecraft.advancements.{CriteriaTriggers, ICriterionInstance, ICriterionTrigger}
import net.minecraftforge.fml.relauncher.ReflectionHelper

object AdvancementTriggers {
  protected var registerTrigger: Method = _

  var guiOpen: AdvancementTrigger[String, _] =
    _

  def init(): Unit = {
    registerTrigger = ReflectionHelper.findMethod(classOf[CriteriaTriggers], "register", "func_192118_a", classOf[ICriterionTrigger[_ <: ICriterionInstance]])
    registerTrigger.setAccessible(true)

    guiOpen = registerAdvancementTrigger(new GUIOpen.Trigger)
  }

  def registerAdvancementTrigger[Data, A <: TriggerInstance[Data]](trigger: AdvancementTrigger[Data, A]): AdvancementTrigger[Data, A] =
    registerTrigger.invoke(null, trigger).asInstanceOf[AdvancementTrigger[Data, A]]
}
