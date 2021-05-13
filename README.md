## SonnetKt

A WIP library for generating Kotlin source files (*.kt) using a DSL style. SonnetKt is a wrapper for the [KotlinPoet][1] library.

For another, actually complete take on DSL Kotlin code generations, see [KRobot][2], which has a very different philosophy

### Goals

* Beauty
* Convenience through reducing noise
* Making impossible or invalid states unrepresentable

### Example

The generated code:
```kotlin
class Greeter(val name: String) {
  fun greet() {
    println("""Hello, $name""")
  }
}

fun main(vararg args: String) {
  Greeter(args[0]).greet()
}
```
SonnetKT (WIP):
```kotlin
val greeterClass = className("Greeter")
val file = File("HelloWorld") {
    defineClass("Greeter") {
        primaryConstructor {
            parameter("name", String::class)
        }

        property("name", String::class) { 
            initializer { +"name" } 
        }

        function("greet") {
            +"println(%P)".with("Hello, \$name")
        }
    }
    
    function("main") {
        parameter("args", String::class) { vararg = true }
        +"%T(args[0]).greet()".with(greeterClass)
    }
}

println(file)
```

The equivalent using KotlinPoet:

```kotlin
val greeterClass = ClassName("", "Greeter")
val file = FileSpec.builder("", "HelloWorld")
    .addType(TypeSpec.classBuilder("Greeter")
            .primaryConstructor(FunSpec.constructorBuilder()
                    .addParameter("name", String::class)
                    .build())
            .addProperty(PropertySpec.builder("name", String::class)
                    .initializer("name")
                    .build())
            .addFunction(FunSpec.builder("greet")
                    .addStatement("println(%P)", "Hello, \$name")
                    .build())
            .build())
    .addFunction(FunSpec.builder("main")
            .addParameter("args", String::class, KModifier.VARARG)
            .addStatement("%T(args[0]).greet()", greeterClass)
            .build())
    .build()

println(file)
```


[1]: https://github.com/square/kotlinpoet/
[2]: https://github.com/NKB03/krobot