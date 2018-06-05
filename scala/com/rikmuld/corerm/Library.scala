package com.rikmuld.corerm

import com.rikmuld.corerm.RMMod._
import net.minecraft.util.ResourceLocation

object Library {
  object TextInfo {
    final val COLOURS_DYE = Array("0", "4", "2", "6", "1", "5", "3", "7", "8", "d", "a", "e", "9", "d", "6", "f")

    final val COLOR_BLACK = "\u00a70"
    final val COLOR_BLUE_DARK = "\u00a71"
    final val COLOR_GREEN_DARK = "\u00a72"
    final val COLOR_AQUA_DARK = "\u00a73"
    final val COLOR_RED_DARK = "\u00a74"
    final val COLOR_PURPLE = "\u00a75"
    final val COLOR_ORANGE = "\u00a76"
    final val COLOR_GRAY = "\u00a77"
    final val COLOR_GRAY_DARK = "\u00a78"
    final val COLOR_BLUE = "\u00a79"
    final val COLOR_GREEN_LIGHT = "\u00a7a"
    final val COLOR_AQUA_LIGHT = "\u00a7b"
    final val COLOR_RED = "\u00a7c"
    final val COLOR_PINK = "\u00a7d"
    final val COLOR_YELLOW = "\u00a7e"
    final val COLOR_WHITE = "\u00a7f"

    final val FORMAT_OBFUSCATED = "\u00a7k"
    final val FORMAT_BOLD = "\u00a7l"
    final val FORMAT_THROUGH = "\u00a7m"
    final val FORMAT_UNDERLINE = "\u00a7n"
    final val FORMAT_ITALIC = "\u00a7o"

    final val RESET = "\u00a7r"
  }

  object TextureInfo {
    final val GUI_LOCATION = MOD_ID + ":textures/gui/"
    final val GUI_TAB_UTILS = GUI_LOCATION + "gui_tab_utils.png"
  }


  object AdvancementInfo {
    final val GUI_OPEN = new ResourceLocation(MOD_ID, "gui_open")
  }

  object Registries {
    final val PACKETS = new ResourceLocation(MOD_ID, "packets")
    final val SCREEN = new ResourceLocation(MOD_ID, "screens")
    final val CONTAINER = new ResourceLocation(MOD_ID, "containers")
    final val ADVANCEMENT_TRIGGERS = new ResourceLocation(MOD_ID, "advancement_triggers")
  }

  object Packets {
    final val TILE_DATA = "tile_data"
    final val BOUNDS = "bounds"
    final val TAB_SWITCH = "tab_switch"
    final val OPEN_GUI = "open_gui"
    final val ITEM_DATA = "item_data"
  }

  object TileEntities {
    final val BOUNDS = MOD_ID + ":bounds"
    final val SIMPLE = MOD_ID + ":simple"
  }
}
