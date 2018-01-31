package com.rikmuld.corerm.advancements

import com.rikmuld.corerm.registry.Registry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

object TriggerHelper {
  def trigger(trigger: ResourceLocation, player: EntityPlayer, data: Any): Unit =
    Registry.advancementRegistry.getValue(trigger).trigger(player, data)
}
