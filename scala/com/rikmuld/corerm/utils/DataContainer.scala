package com.rikmuld.corerm.utils

class DataContainer[T] {
  private var data: Option[T] = None

  def fill(t: T): Unit =
    data = Some(t)

  def get: Option[T] =
    data
}
