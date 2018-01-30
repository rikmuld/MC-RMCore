package com.rikmuld.corerm.utils

import java.nio.ByteBuffer

object MathUtils {
  def intToBytes(int: Int): Array[Byte] =
    ByteBuffer.allocate(4).putInt(int).array

  def bytesToInt(bytes: Seq[Byte]): Int =
    ByteBuffer.wrap(bytes.toArray).getInt

  def isInBox(left: Int, top: Int, width: Int, height: Int)(x: Int, y: Int): Boolean =
    x >= left && x <= left + width && y >= top && y <= top + height

  def getScaledNumber(current: Int, maxNumber: Int, scaledNumber: Int): Float =
    (current.toFloat / maxNumber.toFloat) * scaledNumber

  def bitGet(number: Int, pos:Int, size:Int): Int =
    (Math.pow(2, size).toInt - 1) & (number >> pos)

  def bitPut(number: Int, pos:Int, data:Int): Int =
    number | (data << pos)

  def toInt(bool: Boolean): Int =
    if(bool) 1
    else 0
}
