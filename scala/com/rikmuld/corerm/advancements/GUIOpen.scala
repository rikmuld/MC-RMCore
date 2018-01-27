package com.rikmuld.corerm.advancements

import com.google.gson.{JsonDeserializationContext, JsonObject}
import com.rikmuld.corerm.Lib.AdvancementInfo._
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.{JsonUtils, ResourceLocation}

object GUIOpen {
  class Trigger extends AdvancementTrigger[String, Instance] {
    protected val id: ResourceLocation =
      GUI_OPEN

    override def deserializeInstance(json: JsonObject, context: JsonDeserializationContext): Instance =
      new Instance(JsonUtils.getString(json, "gui"))
  }

  protected class Instance(gui: String) extends TriggerInstance[String](GUI_OPEN) {
    def test(player: EntityPlayerMP, openedGui: String): Boolean =
      openedGui == gui
  }
}