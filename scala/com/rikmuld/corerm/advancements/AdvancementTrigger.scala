package com.rikmuld.corerm.advancements

import com.google.gson.{JsonDeserializationContext, JsonObject}
import net.minecraft.advancements.critereon.AbstractCriterionInstance
import net.minecraft.advancements.{ICriterionTrigger, PlayerAdvancements}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation

import scala.collection.mutable

trait AdvancementTrigger[Data, A <: TriggerInstance[Data]] extends ICriterionTrigger[A] {
  type Listener =
    ICriterionTrigger.Listener[A]

  private val listenerMap: mutable.Map[PlayerAdvancements, Seq[Listener]] =
    mutable.Map[PlayerAdvancements, Seq[Listener]]()

  protected val id: ResourceLocation

  override def getId: ResourceLocation =
    id

  override def addListener(advancements: PlayerAdvancements, listener: Listener): Unit =
    updateListener(advancements)(_ :+ listener)

  override def removeListener(advancements: PlayerAdvancements, listener: Listener): Unit =
    updateListener(advancements)(_.filterNot(_ == listener))

  override def removeAllListeners(advancements: PlayerAdvancements): Unit =
    listenerMap.remove(advancements)

  def updateListener(advancements: PlayerAdvancements)(f: Seq[Listener] => Seq[Listener]): Unit =
    listenerMap(advancements) = f(getListeners(advancements))

  def getListeners(advancements: PlayerAdvancements): Seq[Listener] =
    listenerMap.getOrElse(advancements, Seq())

  override def deserializeInstance(json: JsonObject, context: JsonDeserializationContext): A

  def trigger(player: EntityPlayerMP, data: Data): Unit = {
    getListeners(player.getAdvancements).foreach { instance =>
      if(instance.getCriterionInstance.test(player, data)){
        instance.grantCriterion(player.getAdvancements)
      }
    }
  }
}

abstract class TriggerInstance[Data](id: ResourceLocation) extends AbstractCriterionInstance(id) {
  def test(player: EntityPlayerMP, data: Data): Boolean
}