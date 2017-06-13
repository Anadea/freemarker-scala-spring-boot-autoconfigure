package com.anahoret.freemarker_scala_spring_boot_autoconfigure.freemarker.wrapper

import freemarker.template.{ObjectWrapper, TemplateModel, TemplateSequenceModel}

class ArrayModel[T](val array: Array[T], wrapper: ObjectWrapper)
  extends TemplateSequenceModel {

  def get(index: Int): TemplateModel = wrapper.wrap(array(index))

  def size: Int = array.length
}
