package com.rikmuld.corerm.advancements.triggers

import com.google.gson.{JsonDeserializationContext, JsonObject}
import com.rikmuld.corerm.Library.AdvancementInfo._
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.{JsonUtils, ResourceLocation}

object TriggerOpenGUI {
  class Trigger extends TriggerSimple.Trigger[String, Instance] {
    protected val id: ResourceLocation =
      GUI_OPEN

    override def deserializeInstance(json: JsonObject, context: JsonDeserializationContext): Instance =
      new Instance(JsonUtils.getString(json, "gui"))
  }

  protected class Instance(gui: String) extends TriggerSimple.Instance[String](GUI_OPEN) {
    def test(player: EntityPlayerMP, openedGui: String): Boolean =
      openedGui == gui
  }
}