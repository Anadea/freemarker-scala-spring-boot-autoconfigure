package com.anahoret.freemarker_scala_spring_boot_autoconfigure.freemarker.wrapper

import java.lang.reflect.{Field, Method, Modifier}
import java.util

import freemarker.ext.beans.StringModel
import freemarker.template._

class ScalaWrapper extends ObjectWrapper {

  private val defaultObjectWrapperBuilder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_23)
  defaultObjectWrapperBuilder.setOuterIdentity(this)
  defaultObjectWrapperBuilder.setExposeFields(true)

  private val defaultObjectWrapper = defaultObjectWrapperBuilder.build()

  def wrap(obj: scala.Any): TemplateModel = {
    obj match {
      case null => null
      case option: Option[_] => option match {
        case Some(o) => wrap(o)
        case _ => null
      }
      case model: TemplateModel => model
      case array: Array[_] => new ArrayModel(array, this)
      case seq: Seq[_] => new SeqModel(seq, this)
      case map: Map[_, _] => new MapModel(map, this)
      case iterable: Iterable[_] => new IterableModel(iterable, this)
      case iterator: Iterator[_] => new IteratorModel(iterator, this)
      case date: java.util.Date => defaultObjectWrapper.wrap(date)
      case bool: Boolean => if (bool) TemplateBooleanModel.TRUE else TemplateBooleanModel.FALSE
      case b => new ScalaObjectWrapper(b, this)
    }
  }

  class ScalaMethodWrapper(obj: Any, methodName: String, wrapper: ObjectWrapper) extends TemplateMethodModelEx {
    override def exec(arguments: util.List[_]): AnyRef = {
      val res = defaultObjectWrapper.wrap(obj)
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

    def findField(clazz: Class[_], fieldName: String): Option[Field] = {
      clazz.getDeclaredFields.find(f => f.getName == fieldName && Modifier.isPublic(f.getModifiers)) match {
        case None if clazz != classOf[Object] => findField(clazz.getSuperclass, fieldName)
        case other => other
      }
    }

    def findMethod(clazz: Class[_], methodName: String): Option[Method] = {
      clazz.getDeclaredMethods.find(m => m.getName == methodName && Modifier.isPublic(m.getModifiers)) match {
        case None if clazz != classOf[Object] => findMethod(clazz.getSuperclass, methodName)
        case other => other
      }
    }

    override def get(key: String): TemplateModel = {
      val fieldValue = findField(objectClass, key).map(_.get(obj)).map(wrapper.wrap)
      lazy val methodValue = findMethod(objectClass, key) match {
        case Some(method) if method.getParameterCount == 0 => Some(wrapper.wrap(method.invoke(obj)))
        case Some(_) => Some(new ScalaMethodWrapper(obj, key, wrapper))
        case _ => None
      }
      lazy val getterMethodValue = if (key.startsWith("get") && key.length > 3) {
        val mName = key.slice(3, 4).toLowerCase() + key.drop(4) // remove "get" prefix and uncapitalize first letter
        findMethod(objectClass, mName) match {
          case Some(method) if method.getParameterCount == 0 => Some(new ScalaMethodWrapper(obj, mName, wrapper))
          case _ => None
        }
      } else { None }

      fieldValue
        .orElse(getterMethodValue)
        .orElse(methodValue)
        .getOrElse(defaultObjectWrapper.wrap(null))
    }

    override def isEmpty: Boolean = false
    override def getAsString: String = obj.toString
  }

}
