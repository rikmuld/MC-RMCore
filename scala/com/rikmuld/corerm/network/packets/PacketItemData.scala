package com.rikmuld.corerm.network.packets

import com.rikmuld.corerm.tileentity.TileEntityInventory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class PacketItemData(var slot: Int, var x: Int, var y: Int, var z: Int, var stack: ItemStack) extends PacketBasic {
  def this() =
    this(0, 0, 0, 0, null)

  override def write(stream: PacketBuffer): Unit = {
    stream.writeInt(slot)
    stream.writeInt(x)
    stream.writeInt(y)
    stream.writeInt(z)
    stream.writeItemStack(stack)
  }

  override def read(stream: PacketBuffer): Unit = {
    slot = stream.readInt
    x = stream.readInt
    y = stream.readInt
    z = stream.readInt
    stack = stream.readItemStack()
  }

  override def handlePacket(player: EntityPlayer, ctx: MessageContext): Unit =
    Option(player.world.getTileEntity(new BlockPos(x, y, z))).foreach(tile =>
      tile.asInstanceOf[TileEntityInventory].setInventorySlotContents(slot, stack)
    )
}