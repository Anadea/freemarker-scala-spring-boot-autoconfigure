package com.anahoret.freemarker_scala_spring_boot_autoconfigure.freemarker.wrapper

import freemarker.template.{SimpleNumber, SimpleScalar, TemplateModel}
import org.scalatest.WordSpec

import scala.collection.convert.ImplicitConversions._
import scala.language.implicitConversions

class ScalaWrapperTest extends WordSpec {

  type ScalaObjectWrapper = ScalaWrapper#ScalaObjectWrapper
  type ScalaMethodWrapper = ScalaWrapper#ScalaMethodWrapper

  "A Wrapper" when {
    val wrapper = new ScalaWrapper
    val wrappedUser = wrapper.wrap(new User).asInstanceOf[ScalaObjectWrapper]

    "resolving property with getter by name" should {
      "return result" in {
        assert(wrappedUser.getObjectAsString("name") == "Alex")
      }
    }

    "resolving property with getter by getter method" should {
      "return result" in {
        assert(wrappedUser.getMethodAsString("getName", Nil) == "Alex")
      }
    }

    "resolving property without getter by name" should {
      "return result" in {
        assert(wrappedUser.getObjectAsString("id") == "42")
      }
    }

    "resolving property without getter by getter method" should {
      "return result" in {
        assert(wrappedUser.getMethodAsString("getId", Nil) == "42")
      }
    }

    "resolving overloaded method without parameters" should {
      "return result" in {
        assert(wrappedUser.getMethodAsString("sayHi", Nil) == "Hi, Alex")
      }
    }

    "resolving overloaded method with one parameter" should {
      "return result" in {
        assert(wrappedUser.getMethodAsString("sayHi", List(new SimpleScalar("Bob"))) == "Hi, Bob")
      }
    }

    "resolving overloaded method with two parameters" should {
      "return result" in {
        assert(wrappedUser.getMethodAsString("sayHi", List(new SimpleScalar("Bob"), new SimpleNumber(100))) == "Hi, Bob 100")
      }
    }

    "resolving getter by property name without property" should {
      "return result" in {
        assert(wrappedUser.getObjectAsString("value") == "value")
      }
    }

    "resolving getter by getter name without property" should {
      "return result" in {
        assert(wrappedUser.getMethodAsString("getValue", Nil) == "value")
      }
    }

  }

  class User {
    val id = 42

    var name: String = "Alex"
    def sayHi(): String = s"Hi, $name"
    def sayHi(n: String): String = s"Hi, $n"
    def sayHi(n: String, i: Int): String = s"Hi, $n $i"

    def getName: String = name
    def setName(name: String): Unit = this.name = name

    def getValue: String = "value"

  }

  class ScalaObjectWrapperExt(scalaObjectWrapper: ScalaObjectWrapper) {
    def getObjectAsString(key: String): String =
      scalaObjectWrapper.get(key).asInstanceOf[ScalaObjectWrapper].getAsString

    def getMethodAsString(key: String, args: List[TemplateModel]): String =
      scalaObjectWrapper.get(key).asInstanceOf[ScalaMethodWrapper].exec(args).asInstanceOf[ScalaObjectWrapper].getAsString
  }

  implicit def scalaObjectWrapper2Ext(scalaObjectWrapper: ScalaObjectWrapper): ScalaObjectWrapperExt =
    new ScalaObjectWrapperExt(scalaObjectWrapper)

}
