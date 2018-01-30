package com.rikmuld.corerm.network.packets

import com.rikmuld.corerm.gui.GuiHelper
import com.rikmuld.corerm.registry.Registry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class PacketOpenGui(var id: Int) extends PacketBasic {
  var x: Int = 0
  var y: Int = 0
  var z: Int = 0

  def this() =
    this(0)

  def this(id: Int, x: Int, y: Int, z: Int) {
    this(id)
    this.x = x
    this.y = y
    this.z = z
  }

  def this(name: ResourceLocation, x: Int, y: Int, z: Int) =
    this(Registry.screenRegistry.getID(name), x, y, z)

  def this(name: ResourceLocation, pos: BlockPos) =
    this(Registry.screenRegistry.getID(name), pos.getX, pos.getY, pos.getZ)

  def this(name: ResourceLocation) =
    this(Registry.screenRegistry.getID(name))

  override def write(stream: PacketBuffer) {
    stream.writeInt(id)
    stream.writeInt(x)
    stream.writeInt(y)
    stream.writeInt(z)
  }

  override def read(stream: PacketBuffer) {
    id = stream.readInt
    x = stream.readInt
    y = stream.readInt
    z = stream.readInt
  }

  override def handlePacket(player: EntityPlayer, ctx: MessageContext) =
    GuiHelper.openGui(id, player, x, y, z)
}
