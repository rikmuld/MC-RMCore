//package com.rikmuld.corerm.network.packets
//
//import com.rikmuld.corerm.RMMod
//import com.rikmuld.corerm.utils.CoreUtils._
//import net.minecraft.entity.player.EntityPlayer
//import net.minecraft.network.PacketBuffer
//import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
//
//class PacketGuiSync(guiData: Seq[Int]) extends PacketBasic {
//  var length: Int =
//    guiData.length * 4
//
//  var data: Array[Byte] = {
//    for (i <- guiData.indices)
//      yield intToBytes(guiData(i))
//  }.flatten.toArray
//
//  def this() =
//    this(Seq())
//
//  def this(guiData: Map[Int, Int]) =
//    this(guiData.values.toSeq ++ guiData.keys)
//
//  def write(stream: PacketBuffer) {
//    stream.writeInt(length)
//    stream.writeBytes(data)
//  }
//
//  def read(stream: PacketBuffer) {
//    length = stream.readInt
//    data = new Array[Byte](length)
//    stream.readBytes(data)
//  }
//
//  def handlePacket(player: EntityPlayer, ctx: MessageContext) {
//    val intData =
//      for (i <- 0 until length / 4)
//        yield bytesToInt (
//          for (j <- 0 until 4)
//            yield data(j + (i * 4))
//        )
//
//    val (keys, values) = intData.splitAt(intData.length / 2)
//    val map = (keys zip values).toMap
//
//    RMMod.proxy.screenToContainer = map
//  }
//}