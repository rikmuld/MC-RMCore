package com.rikmuld.corerm.client

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import scala.collection.mutable.HashMap
import net.minecraft.entity.Entity
import scala.collection.mutable.ListBuffer

class RMModel extends ModelBase {
  val boxes = new HashMap[String, ModelRenderer]
  val queue = new ListBuffer[ModelRenderer]
  
  var persistentQueue = false
  
  def this(xTexSize:Int, yTexSize:Int) {
    this

    textureWidth = xTexSize
    textureHeight = yTexSize
  }
  def setTextureSize(x:Int, y:Int){
    textureWidth = x
    textureHeight = y
  }
  def setRotation(model:ModelRenderer, x:Float, y:Float, z:Float) {
    model.rotateAngleX = x
    model.rotateAngleY = y
    model.rotateAngleZ = z
  }
  def addBox(name:String, texOffsetX:Int, texOffsetY:Int, xSize:Int, ySize:Int, zSize:Int, rotPX:Float, rotPY:Float, rotPZ:Float, rotX:Float, rotY:Float, rotZ:Float){
    val box = new ModelRenderer(this, texOffsetX, texOffsetY)
    box.addBox(0F, 0F, 0F, xSize, ySize, zSize)
    box.setRotationPoint(rotPX, rotPY, rotPZ)
    box.setTextureSize(textureWidth, textureHeight)
    box.mirror = true
    setRotation(box, rotX, rotY, rotZ)
    boxes(name) = box
  }
  def renderAll {
    renderAll(null, 1)
  }
  def renderAll(entity:Entity, scale:Float) {
    boxes.foreach {box => queue.append(box._2)}
    render(entity, 0, 0, 0, 0, 0, scale)
  }
  def renderOnly(names:String*) {
    renderOnly(null, 1, names:_*)
  }
  def renderOnly(entity:Entity, scale:Float, names:String*){
    boxes.foreach {box => if(names.contains(box._1))queue.append(box._2)}
    render(entity, 0, 0, 0, 0, 0, scale)
  }
  def renderExcept(names:String*) {
    renderExcept(null, 1, names:_*)
  }
  def renderExcept(entity:Entity, scale:Float, names:String*){
    boxes.foreach {box => if(!names.contains(box._1))queue.append(box._2)}
    render(entity, 0, 0, 0, 0, 0, scale)
  }
  def getBox(name:String) = boxes(name)
  def applyGl() {}
  def setPersistentQueue(persistent:Boolean, fill:Boolean) {
    persistentQueue = persistent
    if(fill)queue.appendAll(boxes.values)
  } 
  override def render(entity: Entity, f2: Float, f3: Float, f4: Float, f5: Float, f6: Float, f7: Float) {
    super.render(entity, f2, f3, f4, f5, f6, f7)
    
    setRotationAngles(f2, f3, f4, f5, f6, f7, entity)
    applyGl
    queue.foreach {box => box.renderWithRotation(f7)}
    if(!persistentQueue)queue.clear
  }
}