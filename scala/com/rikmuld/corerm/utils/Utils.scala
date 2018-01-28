package com.rikmuld.corerm.utils

import java.util.{ArrayList, Random}

import com.rikmuld.corerm.utils.WorldBlock._
import net.minecraft.block.{Block, BlockFence}
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.entity.{EntityList, EntityLivingBase}
import net.minecraft.init.Blocks
import net.minecraft.inventory.IInventory
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{BlockPos, MathHelper, RayTraceResult, Vec3d}
import net.minecraft.world.World
import net.minecraftforge.oredict.OreDictionary

import scala.collection.JavaConversions._
import scala.collection.mutable.{ListBuffer, WrappedArray}

object CoreUtils {
  def isInBox(left: Int, top: Int, width: Int, height: Int)(x: Int, y: Int) =
    x >= left && x <= left + width && y >= top && y <= top + height

  var startEntityId = 300

  def getUniqueEntityId = {
    while (EntityList.getClassFromID(startEntityId) != null) startEntityId += 1
    startEntityId
  }
  def nwsk(item:Item) = new ItemStack(item)
  def nwsk(item:Block) = new ItemStack(item)
  def nwsk(item:Item, meta:Int) = new ItemStack(item, 1, meta)
  def nwsk(item:Block, meta:Int) = new ItemStack(item, 1, meta)
  def nwsk(item:Item, size:Int, meta:Int) = new ItemStack(item, size, meta)
  def nwsk(item:Block, size:Int, meta:Int) = new ItemStack(item, size, meta)
  implicit class ItemUtils(obj: AnyRef) {
    def toStack(): ItemStack = {
      var stack: ItemStack = null
      if (obj.isInstanceOf[Block]) stack = new ItemStack(obj.asInstanceOf[Block])
      else if (obj.isInstanceOf[Item]) stack = new ItemStack(obj.asInstanceOf[Item])
      else if (obj.isInstanceOf[ItemStack]) stack = obj.asInstanceOf[ItemStack]
      stack
    }
    def toStack(count: Int): ItemStack = {
      val stack = toStack
      stack.setCount(count)
      stack
    }
    def getMetaCycle(maxMetadata: Int): Array[ItemStack] = {
      val stack = Array.ofDim[ItemStack](maxMetadata)
      for (i <- 0 until maxMetadata) {
        if (obj.isInstanceOf[Block]) stack(i) = new ItemStack(obj.asInstanceOf[Block], 1, i)
        if (obj.isInstanceOf[Item]) stack(i) = new ItemStack(obj.asInstanceOf[Item], 1, i)
      }
      stack
    }
  }
  implicit class WorldUtils(world: World) {
    def dropItemInWorld(itemStack: ItemStack, x: Float, y: Float, z: Float, rand: Random) {
      if (!world.isRemote) {
        if ((itemStack != null) && (itemStack.getCount > 0)) {
          val dX = (rand.nextFloat() * 0.8F) + 0.1F
          val dY = (rand.nextFloat() * 0.8F) + 0.1F
          val dZ = (rand.nextFloat() * 0.8F) + 0.1F
          val entityItem = new EntityItem(world, x + dX, y + dY, z + dZ, new ItemStack(itemStack.getItem, itemStack.getCount, itemStack.getItemDamage))
          if (itemStack.hasTagCompound()) entityItem.getItem.setTagCompound(itemStack.getTagCompound.copy().asInstanceOf[NBTTagCompound])
          val factor = 0.05F
          entityItem.motionX = rand.nextGaussian() * factor
          entityItem.motionY = (rand.nextGaussian() * factor) + 0.2F
          entityItem.motionZ = rand.nextGaussian() * factor
          world.spawnEntity(entityItem)
          itemStack.setCount(0)
        }
      }
    }
    def isTouchingBlockSolidForSide(pos:BlockPos, facing: EnumFacing): Boolean = {
      val bd = (world, pos.offset(facing))
      bd.block != Blocks.AIR && (bd.world.isSideSolid(bd.pos, facing.getOpposite) || bd.block.isInstanceOf[BlockFence] || bd.block.isFullCube(bd.state) )
    }
    def dropItemsInWorld(stacks: ArrayList[ItemStack], x: Float, y: Float, z: Float, rand: Random) {
      if (!world.isRemote) {
        for (i <- 0 until stacks.size) {
          val itemStack = stacks(i)
          dropItemInWorld(itemStack, x, y, z, rand)
        }
      }
    }
    def dropItemsInWorld(stacks: Array[ItemStack], x: Float, y: Float, z: Float, rand: Random) {
      if (!world.isRemote) {
        for (i <- 0 until stacks.size) {
          val itemStack = stacks(i)
          dropItemInWorld(itemStack, x, y, z, rand)
        }
      }
    }
    def dropBlockItems(pos:BlockPos, rand: Random) {
      if (!world.isRemote) {
        val tileEntity = world.getTileEntity(pos)
        if (tileEntity.isInstanceOf[IInventory]) {
          val inventory = tileEntity.asInstanceOf[IInventory]
          for (i <- 0 until inventory.getSizeInventory) {
            val itemStack = inventory.getStackInSlot(i)
            if ((itemStack != null) && (itemStack.getCount > 0)) {
              val dX = (rand.nextFloat() * 0.8F) + 0.1F
              val dY = (rand.nextFloat() * 0.8F) + 0.1F
              val dZ = (rand.nextFloat() * 0.8F) + 0.1F
              val entityItem = new EntityItem(world, pos.getX + dX, pos.getY + dY, pos.getZ + dZ, new ItemStack(itemStack.getItem,
                itemStack.getCount, itemStack.getItemDamage))
              if (itemStack.hasTagCompound()) {
                entityItem.getItem.setTagCompound(itemStack.getTagCompound.copy())
              }
              val factor = 0.05F
              entityItem.motionX = rand.nextGaussian() * factor
              entityItem.motionY = (rand.nextGaussian() * factor) + 0.2F
              entityItem.motionZ = rand.nextGaussian() * factor
              world.spawnEntity(entityItem)
              itemStack.setCount(0)
            }
          }
        }
      }
    }
  }

  implicit class PlayerUtils(player: EntityPlayer) {
    def setCurrentItem(stack: ItemStack) = player.inventory.setInventorySlotContents(player.inventory.currentItem, stack)
    def getMOP(): RayTraceResult = {
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
  
  implicit class InventoryUtils(stacks: ArrayList[ItemStack]) {
    def getNBT(): NBTTagList = {
      val inventory = new NBTTagList()
      for (slot <- 0 until stacks.size if stacks.get(slot) != null) {
        val Slots = new NBTTagCompound()
        Slots.setByte("Slot", slot.toByte)
        stacks.get(slot).writeToNBT(Slots)
        inventory.appendTag(Slots)
      }
      inventory
    }
    def containsItem(item: Item): Boolean = stacks.find(it=> it!=null && it.getItem == item).isDefined
    def containsStack(stack: ItemStack): Boolean = stacks.find(it=> it!=null && it.isItemEqual(stack)).isDefined
  }

  implicit class ItemStackUtils(item: ItemStack) {
    def addDamage(player: EntityPlayer, damage: Int) {
      val returnStack = new ItemStack(item.getItem, 1, (item.getItemDamage + damage))
      player.inventory.setInventorySlotContents(player.inventory.currentItem, if ((returnStack.getItemDamage >= item.getMaxDamage)) null else returnStack)
    }
    def getWildValue = new ItemStack(item.getItem(), 1, OreDictionary.WILDCARD_VALUE)
    def addDamage(damage: Int): ItemStack = {
      val returnStack = new ItemStack(item.getItem, 1, (item.getItemDamage + damage))
      val returnStack2 = returnStack.copy()
      returnStack2.setCount(0)
      if (returnStack.getItemDamage >= item.getMaxDamage) returnStack2 else returnStack
    }
  }

  implicit class IntArrayUtils(numbers: Array[Int]) {
    def inverse(): Array[Int] = {
      val returnNumbers = Array.ofDim[Int](numbers.length)
      for (i <- 0 until numbers.length) returnNumbers(i) = -numbers(i)
      returnNumbers
    }
  }
  
  implicit class WrappedArrayUtils(wrap:WrappedArray[_]) {
    def unwrap:ListBuffer[Any] = {
      val retArr = new ListBuffer[Any]
      for(arr <- wrap){
        if(arr.isInstanceOf[WrappedArray[_]])retArr.append(arr.asInstanceOf[WrappedArray[_]].unwrap)
        else retArr.append(arr)
      }
      retArr
    }
  }
  
  implicit class BlockUtils(block:Block){
    def getBounds(world:World, pos:BlockPos) = {
      val bd = (world, pos)
      val bounds = bd.state.getCollisionBoundingBox(world, pos) 
      if(bounds != null) Array(bounds.minY-bd.y, bounds.maxY-bd.y, bounds.maxZ-bd.z, bounds.minZ-bd.z, bounds.minX-bd.x, bounds.maxX-bd.x) else null
    }
  }

  implicit class IntegerUtils(currNumber: Int) {
    def getScaledNumber(maxNumber: Int, scaledNumber: Int): Float = (currNumber.toFloat / maxNumber.toFloat) * scaledNumber
    def bitGet(pos:Int, size:Int) = (Math.pow(2, size).toInt - 1) & (currNumber >> pos)
    def bitPut(pos:Int, data:Int) = currNumber | (data << pos)
  }
  
  implicit class LivingUtils(entity:EntityLivingBase) {
    def facing:EnumFacing = {
      val facing = MathHelper.floor(((entity.rotationYaw * 4.0F) / 360.0F) + 0.5D) & 3
      if (facing == EnumFacing.WEST.getHorizontalIndex) EnumFacing.WEST
      else if (facing == EnumFacing.SOUTH.getHorizontalIndex) EnumFacing.SOUTH
      else if (facing == EnumFacing.NORTH.getHorizontalIndex) EnumFacing.NORTH
      else if (facing == EnumFacing.EAST.getHorizontalIndex) EnumFacing.EAST
      else EnumFacing.NORTH
    }
  }
  implicit class BoolUtils(value:Boolean) {
    def intValue = if(value) 1 else 0
  }
}