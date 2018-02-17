package com.rikmuld.corerm.tileentity

import com.rikmuld.corerm.tileentity.TileEntityTicker._
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable

import scala.collection.mutable

object TileEntityTicker {
  type Ticker = () => Unit
}

trait TileEntityTicker extends ITickable {
  val tickers: mutable.Map[Int, Seq[Ticker]] =
    mutable.Map()

  var ticks =
    0

  def registerTicker(ticker: Ticker, tickDelay: Int): Unit =
    tickers(tickDelay) =
      tickers.getOrElse(tickDelay, Vector()) :+ ticker

  override def update(): Unit = {
    tickers.foreach {
      case(tickTime, tickerList) =>
        if(ticks % tickTime == 0)
          tickerList.foreach(_())
    }

    ticks += 1
  }

  def writeNBT(tag: NBTTagCompound): NBTTagCompound = {
    tag.setInteger("ticks", ticks)
    tag
  }

  def readNBT(tag: NBTTagCompound): Unit = {
    ticks = tag.getInteger("ticks")
  }
}
