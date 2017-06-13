package com.anahoret.freemarker_scala_spring_boot_autoconfigure.freemarker.wrapper

import freemarker.template.{ObjectWrapper, TemplateModel, TemplateSequenceModel}

class SeqModel[T](val seq: Seq[T], wrapper: ObjectWrapper) extends TemplateSequenceModel {
  def get(index: Int): TemplateModel = wrapper.wrap(seq(index))
  def size: Int = seq.size
}
