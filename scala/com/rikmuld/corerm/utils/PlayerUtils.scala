package com.rikmuld.corerm.utils

import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.ItemStack
import net.minecraft.util.math.{MathHelper, RayTraceResult, Vec3d}

object PlayerUtils {
  def setCurrentItem(player: EntityPlayer, stack: ItemStack): Unit =
    player.inventory.setInventorySlotContents(player.inventory.currentItem, stack)

  //TODO
  def getMOP(player: EntityPlayer): RayTraceResult = {
    val f = 1.0F
    val f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f
    val f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f
    val d0 = player.prevPosX + (player.posX - player.prevPosX) * f.toDouble
    val d1 = player.prevPosY + (player.posY - player.prevPosY) * f.toDouble + (if (player.world.isRemote) player.getEyeHeight - player.getDefaultEyeHeight else player.getEyeHeight).toDouble
    val d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f.toDouble
    val Vec3d = new Vec3d(d0, d1, d2)
    val f3 = MathHelper.cos(-f2 * 0.017453292F - Math.PI.toFloat)
    val f4 = MathHelper.sin(-f2 * 0.017453292F - Math.PI.toFloat)
    val f5 = -MathHelper.cos(-f1 * 0.017453292F)
    val f6 = MathHelper.sin(-f1 * 0.017453292F)
    val f7 = f4 * f5
    val f8 = f3 * f5
    var d3 = 5.0D
    if (player.isInstanceOf[EntityPlayerMP]) {
      d3 = player.asInstanceOf[EntityPlayerMP].interactionManager.getBlockReachDistance
    }
    val Vec3d1 = Vec3d.addVector(f7.toDouble * d3, f6.toDouble * d3, f8.toDouble * d3)
    player.world.rayTraceBlocks(Vec3d, Vec3d1, true, false, false)
  }
}
