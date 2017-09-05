package com.anahoret.freemarker_scala_spring_boot_autoconfigure.freemarker.wrapper

import java.{ lang, util }

import com.anahoret.freemarker_scala_spring_boot_autoconfigure.models.SomeOuter
import freemarker.template._
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
        assert(wrappedUser.getObjectAsNumber("id").intValue() == 42)
      }
    }

    "resolving property without getter by getter method" should {
      "return result" in {
        assert(wrappedUser.getMethodAsNumber("getId", Nil).intValue() == 42)
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

    "resolving public method of private inner class" should {
      "return result" in {
        val wrapper = new ScalaWrapper
        val wrappedSomeTrait = wrapper.wrap(new SomeOuter().getSomeInterface).asInstanceOf[ScalaObjectWrapper]
        assert(wrappedSomeTrait.getObjectAsString("something") == "Something")
      }
    }

    "resolving property with 'is' getter by name" should {
      "return result" in {
        assert(wrappedUser.getObjectAsBoolean("admin"))
      }
    }

    "resolving property with 'is' getter by getter method" should {
      "return result" in {
        assert(wrappedUser.getMethodAsBoolean("isAdmin", Nil))
      }
    }

    "resolving 'is' getter by property name without property" should {
      "return result" in {
        assert(wrappedUser.getObjectAsBoolean("active"))
      }
    }

    "resolving 'is' getter by getter name without property" should {
      "return result" in {
        assert(wrappedUser.getMethodAsBoolean("isActive", Nil))
      }
    }

    "resolving boolean property without getter by name" should {
      "return result" in {
        assert(wrappedUser.getObjectAsBoolean("banned"))
      }
    }

    "resolving boolean property without getter by getter method" should {
      "return result" in {
        assert(wrappedUser.getMethodAsBoolean("isBanned", Nil))
      }
    }

  }

  "A wrapper" should {
    "resolve java list to list model" in {
      val jList = new util.ArrayList[String]()
      jList.add("One")
      jList.add("Two")
      val wrappedJList = wrap[DefaultListAdapter](jList)

      assert(wrappedJList.size() == 2)
      assert(wrappedJList.get(0).asInstanceOf[SimpleScalar].getAsString == "One")
      assert(wrappedJList.get(1).asInstanceOf[SimpleScalar].getAsString == "Two")
    }

    "resolve java set to sequence model" in {
      val jSet = new util.HashSet[String]()
      jSet.add("One")
      jSet.add("Two")
      val wrappedJSet = wrap[SimpleSequence](jSet)

      assert(wrappedJSet.size() == 2)
      assert(wrappedJSet.get(0).asInstanceOf[SimpleScalar].getAsString != wrappedJSet.get(1).asInstanceOf[SimpleScalar].getAsString)
    }

    "resolve java map to map model" in {
      val jMap = new util.HashMap[String, Integer]()
      jMap.put("One", 1)
      jMap.put("Two", 2)
      val wrappedJMap = wrap[DefaultMapAdapter](jMap)

      assert(wrappedJMap.size() == 2)
      assert(wrappedJMap.get("One").asInstanceOf[SimpleNumber].getAsNumber == 1.asInstanceOf[Number])
      assert(wrappedJMap.get("Two").asInstanceOf[SimpleNumber].getAsNumber == 2.asInstanceOf[Number])
    }

    "resolve java iterator to iterator model" in {
      val jSet = new util.HashSet[String]()
      jSet.add("One")
      jSet.add("Two")
      val wrappedJIterator = wrap[DefaultIteratorAdapter](jSet.iterator())

      val iterator = wrappedJIterator.iterator()
      assert(iterator.next() != iterator.next())
    }

    "resolve java iterable to iterator model" in {
      val jIterable = new lang.Iterable[String] {
        override def iterator(): util.Iterator[String] = new java.util.Iterator[String]() {
          var i = 0
          override def hasNext: Boolean = i < 2

          override def next(): String = {
            i match {
              case 0 => i += 1; "One"
              case 1 => i += 1; "Two"
              case _ => throw new NoSuchElementException
            }
          }
        }
      }
      val wrappedJIterable = wrap[DefaultIteratorAdapter](jIterable)

      val iterator = wrappedJIterable.iterator()
      assert(iterator.next() != iterator.next())
    }

    "resolve java array to array model" in {
      val wrappedJArray = wrap[DefaultArrayAdapter](new SomeOuter().wrappedArray(new ScalaWrapper))

      assert(wrappedJArray.size() == 2)
      assert(wrappedJArray.get(0).asInstanceOf[SimpleScalar].getAsString == "One")
      assert(wrappedJArray.get(1).asInstanceOf[SimpleScalar].getAsString == "Two")
    }

    "resolve java number to number model" in {
      val wrappedJNumber = wrap[SimpleNumber](new lang.Long(100))

      assert(wrappedJNumber.getAsNumber.longValue() == 100L)
    }

    "resolve java boolean to boolean model" in {
      val jBoolean = java.lang.Boolean.TRUE
      val wrappedJBoolean = wrap[TemplateBooleanModel](jBoolean)

      assert(wrappedJBoolean.getAsBoolean)
    }

    "wrap collection items" in {
      val jList = new util.ArrayList[User]()
      jList.add(new User)
      val wrappedJList = wrap[DefaultListAdapter](jList)

      val num = wrappedJList.get(0).asInstanceOf[ScalaObjectWrapper].get("jLong").asInstanceOf[SimpleNumber].getAsNumber
      assert(num.longValue() == 100)
    }
  }

  private def wrap[T](obj: Any): T =
    new ScalaWrapper().wrap(obj).asInstanceOf[T]

  class User {
    val id = 42
    val admin = true
    val isBanned = true
    var jLong = new lang.Long(100)

    var name: String = "Alex"
    def sayHi(): String = s"Hi, $name"
    def sayHi(n: String): String = s"Hi, $n"
    def sayHi(n: String, i: Int): String = s"Hi, $n $i"

    def getName: String = name
    def setName(name: String): Unit = this.name = name

    def getValue: String = "value"
    def isAdmin: Boolean = admin
    def isActive: Boolean = true

  }

  class ScalaObjectWrapperExt(scalaObjectWrapper: ScalaObjectWrapper) {
    def getObjectAsString(key: String): String =
      scalaObjectWrapper.get(key).asInstanceOf[SimpleScalar].getAsString

    def getObjectAsNumber(key: String): Number =
      scalaObjectWrapper.get(key).asInstanceOf[SimpleNumber].getAsNumber

    def getObjectAsBoolean(key: String): Boolean =
      scalaObjectWrapper.get(key).asInstanceOf[TemplateBooleanModel].getAsBoolean

    def getMethodAsString(key: String, args: List[TemplateModel]): String =
      scalaObjectWrapper.get(key).asInstanceOf[ScalaMethodWrapper].exec(args).asInstanceOf[SimpleScalar].getAsString

    def getMethodAsNumber(key: String, args: List[TemplateModel]): Number =
      scalaObjectWrapper.get(key).asInstanceOf[ScalaMethodWrapper].exec(args).asInstanceOf[SimpleNumber].getAsNumber

    def getMethodAsBoolean(key: String, args: List[TemplateModel]): Boolean =
      scalaObjectWrapper.get(key).asInstanceOf[ScalaMethodWrapper].exec(args).asInstanceOf[TemplateBooleanModel].getAsBoolean

  }

  implicit def scalaObjectWrapper2Ext(scalaObjectWrapper: ScalaObjectWrapper): ScalaObjectWrapperExt =
    new ScalaObjectWrapperExt(scalaObjectWrapper)

}
