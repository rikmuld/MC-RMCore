package com.rikmuld.corerm.client

import net.minecraft.client.Minecraft
import net.minecraft.client.model.{ModelBase, ModelRenderer}
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

import scala.collection.mutable

class ModularModal extends ModelBase {
  val boxes: mutable.Map[String, ModelRenderer] =
    mutable.Map[String, ModelRenderer]()

  var renderList: Seq[String] =
    Seq()

  private var scale: Float =
    0.0625f

  private var texture: Option[ResourceLocation] =
    None

  def setTextureSize(width: Int, height: Int): Unit = {
    textureWidth = width
    textureHeight = height
  }

  def setScale(scale: Float): Unit =
    this.scale = scale

  def bindTexture(location: ResourceLocation): Unit =
    texture = Some(location)

  def addBox(name: String, texX:Int, texY:Int, x: Int, y: Int, z: Int, width: Int, height: Int, depth: Int): Unit = {
    val box = new ModelRenderer(this, texX, texY)

    box.addBox(x, y, z, width, height, depth)
    box.setTextureSize(textureWidth, textureHeight)

    boxes(name) = box
  }

  def addBox(name:String, texX:Int, texY:Int, width:Int, height:Int, depth:Int, rotPX:Float, rotPY:Float, rotPZ:Float, rotX:Float, rotY:Float, rotZ:Float){
    val box = new ModelRenderer(this, texX, texY)

    box.addBox(0F, 0F, 0F, width, height, depth)
    box.setTextureSize(textureWidth, textureHeight)
    box.mirror = true

    box.setRotationPoint(rotPX, rotPY, rotPZ)
    box.rotateAngleX = rotX
    box.rotateAngleY = rotY
    box.rotateAngleZ = rotZ

    boxes(name) = box
  }

  def getBox(name:String): ModelRenderer =
    boxes(name)

  def setRenderAll(): Unit =
    renderList = boxes.keySet.toSeq

  def setRenderOnly(names:String*): Unit =
    renderList = names

  def setRenderExcept(exclude:String*): Unit =
    renderList = boxes.keySet.filter(key => !exclude.contains(key)).toSeq

  def renderAll(): Unit =
    render_do(
      boxes
    )

  def renderOnly(names:String*): Unit =
    render_do(
      boxes.filter(box => names.contains(box._1))
    )

  def renderExcept(exclude:String*): Unit =
    render_do(
      boxes.filter(box => !exclude.contains(box._1))
    )

  private def render_do(boxes: mutable.Map[String, ModelRenderer]): Unit = {
    texture.foreach(loc =>
      Minecraft.getMinecraft.renderEngine.bindTexture(loc)
    )

    boxes.foreach(_._2.render(scale))
  }

  def transform(): Unit =
    Unit

  override def render(entity: Entity,
                      limbSwing: Float,
                      limbSwingAmount: Float,
                      ageInTicks: Float,
                      netHeadYaw: Float,
                      headPitch: Float,
                      scale: Float) {

    setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity)
    transform()
    
    boxes.filter(box => renderList.contains(box._1)).foreach(_._2.renderWithRotation(scale))
  }

  def renderBoxes(scale: Float): Unit =
    boxes.filter(box => renderList.contains(box._1)).foreach(_._2.renderWithRotation(scale))
}