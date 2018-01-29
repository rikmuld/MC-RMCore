package com.rikmuld.corerm.advancements

import java.lang.reflect.Method

import com.rikmuld.corerm.advancements.triggers.TriggerSimple
import net.minecraft.advancements.{CriteriaTriggers, ICriterionInstance, ICriterionTrigger}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.ReflectionHelper

import scala.collection.mutable

class TriggerRegistry {
  protected val registerTrigger: Method =
    ReflectionHelper.findMethod(classOf[CriteriaTriggers], "register", "func_192118_a", classOf[ICriterionTrigger[_ <: ICriterionInstance]])

  private val triggers: mutable.Map[ResourceLocation, TriggerSimple.Trigger[_, _]] =
    mutable.Map()

  registerTrigger.setAccessible(true)

  def register[Data, A <: TriggerSimple.Instance[Data]](trigger: TriggerSimple.Trigger[Data, A]): TriggerSimple.Trigger[Data, A] = {
    triggers.put(trigger.getId, trigger)
    registerTrigger.invoke(null, trigger).asInstanceOf[TriggerSimple.Trigger[Data, A]]
  }

  def registerAll(trigger: TriggerSimple.Trigger[_, _]*): Unit = trigger.foreach { trigger =>
    triggers.put(trigger.getId, trigger)
    registerTrigger.invoke(null, trigger)
  }

  def getValue(name: ResourceLocation): TriggerSimple.Trigger[_, _] =
    triggers(name)
}