### Scala objects wrapper Spring Boot autoconfiguration for Freemarker library

This autoconfiguration purpose is to wrap scala objects to Freemarker TemplateModel's.
Following scala types are supported:
1. arrays
2. iterables
3. iterators
4. maps
5. sequences
6. Options
7. Booleans

Given the following class
```
class User {
    val id = 100500
    var name: String = ""
    def sayHi(): String = s"Hi, $name"
    def sayHi(n: String): String = s"Hi, $n"
    def sayHi(n: String, i: Int): String = s"Hi, $n $i"
    def getName: String = name
    def setName(name: String): Unit = this.name = name
  }
```
it allows you to access Users's methods and properties like this:
```
${user.id}
${user.getId()}
${user.name}
${user.getName()}
${user.sayHi()}
${user.sayHi("Bob")}
${user.sayHi("Bill", 100)}
``` 

### Usage

1. Add dependency to your _Maven_
    ```
    <dependency>
        <groupId>com.github.anadea</groupId>
        <artifactId>freemarker-scala-spring-boot-autoconfigure</artifactId>
        <version>1.1-RELEASE</version>
    </dependency>
    ```
    or _Gradle_
    ```
    compile group: 'com.github.anadea', name: 'freemarker-scala-spring-boot-autoconfigure', version: '1.1-RELEASE'
    ```

2. Add following key to _application.properties_:
    ```
    freemarker.use_scala_wrapper = true
    ```
    or _application.yml_
    ```
    freemarker.use_scala_wrapper: true
    ```

### How to build and deploy the library

Build command:
```
./gradlew build
```


Deploy command:
```
./gradlew uploadArchives
```

