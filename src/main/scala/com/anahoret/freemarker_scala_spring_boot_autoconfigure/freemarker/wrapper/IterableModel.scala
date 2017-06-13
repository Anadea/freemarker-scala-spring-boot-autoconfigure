package com.anahoret.freemarker_scala_spring_boot_autoconfigure.freemarker.wrapper

import freemarker.template.{ObjectWrapper, TemplateCollectionModel, TemplateModelIterator}

class IterableModel(iterable: Iterable[_], wrapper: ObjectWrapper) extends TemplateCollectionModel {
  def iterator(): TemplateModelIterator = new IteratorModel(iterable.toIterator, wrapper)
}
