package com.rikmuld.corerm.network.packets

import com.rikmuld.corerm.gui.GuiHelper
import com.rikmuld.corerm.registry.Registry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldServer
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

  /* Scala 2.12 version
  override def handlePacket(player: EntityPlayer, ctx: MessageContext): Unit = {
    val doOpen: Runnable = () => GuiHelper.openGui(id, player, x, y, z)

    if (!player.world.isRemote)
      player.world.asInstanceOf[WorldServer].addScheduledTask(doOpen)
    else
      doOpen()
  }
  */

  // TODO replace below with 2.12 version after updating to scala 2.12
  override def handlePacket(player: EntityPlayer, ctx: MessageContext): Unit = {
    val doOpen: Runnable = new Runnable {
      override def run(): Unit =
        GuiHelper.openGui(id, player, x, y, z)
    }

    if (!player.world.isRemote)
      player.world.asInstanceOf[WorldServer].addScheduledTask(doOpen)
    else
      GuiHelper.openGui(id, player, x, y, z)
  }
}
