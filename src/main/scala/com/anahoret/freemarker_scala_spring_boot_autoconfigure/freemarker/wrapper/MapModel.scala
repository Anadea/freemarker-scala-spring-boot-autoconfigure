package com.anahoret.freemarker_scala_spring_boot_autoconfigure.freemarker.wrapper

import freemarker.template._

class MapModel(map: Map[_, _], wrapper: ObjectWrapper) extends TemplateHashModelEx {
  def get(key: String): TemplateModel = wrapper.wrap(map.asInstanceOf[Map[String, _]].get(key))
  def isEmpty: Boolean = map.isEmpty
  def size(): Int = map.size
  def keys(): TemplateCollectionModel = new IterableModel(map.keys, wrapper)
  def values(): TemplateCollectionModel = new IterableModel(map.values, wrapper)
}
