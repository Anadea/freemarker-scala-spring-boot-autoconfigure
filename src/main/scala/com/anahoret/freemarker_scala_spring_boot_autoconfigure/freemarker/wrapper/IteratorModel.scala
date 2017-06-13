package com.anahoret.freemarker_scala_spring_boot_autoconfigure.freemarker.wrapper

import freemarker.template.{ObjectWrapper, TemplateCollectionModel, TemplateModel, TemplateModelIterator}

class IteratorModel(it: Iterator[_], wrapper: ObjectWrapper) extends TemplateModelIterator with TemplateCollectionModel {

  def next: TemplateModel = wrapper.wrap(it.next())

  def hasNext: Boolean = it.hasNext

  def iterator: IteratorModel = this
}
