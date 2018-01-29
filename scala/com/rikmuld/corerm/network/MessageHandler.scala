package com.rikmuld.corerm.network

import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.Side

class MessageHandler extends IMessageHandler[PacketGlobal, IMessage] {
  override def onMessage(message: PacketGlobal, ctx: MessageContext): IMessage = ctx.side match {
    case Side.SERVER =>
      message.handlePacketServer(ctx)
      null
    case Side.CLIENT =>
      message.handlePacketClient(ctx)
      null
  }
}