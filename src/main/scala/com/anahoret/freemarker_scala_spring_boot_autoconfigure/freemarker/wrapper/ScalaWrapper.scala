package com.anahoret.freemarker_scala_spring_boot_autoconfigure.freemarker.wrapper

import java.lang.reflect.{Field, Method, Modifier}
import java.util

import freemarker.ext.beans.StringModel
import freemarker.template._

class ScalaWrapper extends DefaultObjectWrapper(Configuration.VERSION_2_3_23) {

  setOuterIdentity(this)
  setExposeFields(true)

  override def wrap(obj: scala.Any): TemplateModel = {
    obj match {
      case null => super.wrap(null)
      case str: String => super.wrap(str)
      case jCollection: util.Collection[_] => super.wrap(jCollection)
      case jMap: util.Map[_, _] => super.wrap(jMap)
      case jIterable: java.lang.Iterable[_] => super.wrap(jIterable.iterator())
      case jIterator: util.Iterator[_] => super.wrap(jIterator)
      case date: java.util.Date => super.wrap(date)
      case jNumber: java.lang.Number => super.wrap(jNumber)
      case array: Array[_] => super.wrap(array)
      case option: Option[_] => option match {
        case Some(o) => wrap(o)
        case _ => null
      }
      case model: TemplateModel => model
      case seq: Seq[_] => new SeqModel(seq, this)
      case map: Map[_, _] => new MapModel(map, this)
      case iterable: Iterable[_] => new IterableModel(iterable, this)
      case iterator: Iterator[_] => new IteratorModel(iterator, this)
      case bool: Boolean => if (bool) TemplateBooleanModel.TRUE else TemplateBooleanModel.FALSE
      case b => new ScalaObjectWrapper(b, this)
    }
  }

  class ScalaMethodWrapper(obj: Any, methodName: String, wrapper: ObjectWrapper) extends TemplateMethodModelEx {
    override def exec(arguments: util.List[_]): AnyRef = {
      val res = ScalaWrapper.super.wrap(obj)
        .asInstanceOf[StringModel]
        .get(methodName)
        .asInstanceOf[TemplateMethodModelEx]
        .exec(arguments)
      wrapper.wrap(res)
    }
  }

  class ScalaObjectWrapper(val obj: Any, val wrapper: ObjectWrapper)
    extends TemplateHashModel with TemplateScalarModel {

    private val objectClass = obj.getClass

    override def get(key: String): TemplateModel = {
      val fieldValue = findField(objectClass, key).map { field =>
        field.setAccessible(true)
        field.get(obj)
      }.map(wrapper.wrap)
      val methodValue = findMethod(objectClass, key) match {
        case Some(method) if method.getParameterCount == 0 =>
          method.setAccessible(true)
          Some(wrapper.wrap(method.invoke(obj)))
        case Some(_) => Some(new ScalaMethodWrapper(obj, key, wrapper))
        case _ => None
      }
      val getterMethodValue = findGetter(key, "get")
      val isGetterMethodValue = findGetter(key, "is")

      fieldValue
        .orElse(getterMethodValue)
        .orElse(isGetterMethodValue)
        .orElse(methodValue)
        .getOrElse(ScalaWrapper.super.wrap(null))
    }

    private def findGetter(key: String, prefix: String): Option[TemplateModel] = {
      val prefixLength = prefix.length
      if (key.startsWith(prefix) && key.length > prefixLength) {
        findNoParameterMethod(objectClass, key)
          .orElse {
            val mName = key.slice(prefixLength, prefixLength + 1).toLowerCase() + key.drop(prefixLength + 1) // remove prefix and uncapitalize first letter
            findNoParameterMethod(objectClass, mName)
          }
      } else {
        findMethod(objectClass, prefix + key.capitalize) match {
          case Some(method) if method.getParameterCount == 0 =>
            method.setAccessible(true)
            Some(wrapper.wrap(method.invoke(obj)))
          case _ => None
        }
      }
    }

    private def findField(clazz: Class[_], fieldName: String): Option[Field] = {
      clazz.getDeclaredFields.find(f => f.getName == fieldName && Modifier.isPublic(f.getModifiers)) match {
        case None if clazz != classOf[Object] => findField(clazz.getSuperclass, fieldName)
        case other => other
      }
    }

    private def findMethod(clazz: Class[_], methodName: String): Option[Method] = {
      clazz.getDeclaredMethods.find(m => m.getName == methodName && Modifier.isPublic(m.getModifiers)) match {
        case None if clazz != classOf[Object] => findMethod(clazz.getSuperclass, methodName)
        case other => other
      }
    }

    private def findNoParameterMethod(clazz: Class[_], methodName: String): Option[ScalaMethodWrapper] = {
      findMethod(clazz, methodName)
        .filter(_.getParameterCount == 0)
        .map(_ => new ScalaMethodWrapper(obj, methodName, wrapper))
    }

    override def isEmpty: Boolean = false
    override def getAsString: String = obj.toString
  }

}
