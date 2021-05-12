@file:Suppress("unused")

package sonnetkt

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

fun main() {
    example11Sonnet()
}

fun example11Sonnet() {
    val helloWorld = EnumClass("Roshambo") {
        primaryConstructor {
            parameter("handsign", String::class)
        }
        "ROCK" {
            superConstructor("fist".lit())
            function("toString", String::class) {
                override()
                +"return %S".with("avalanche!")
            }
        }
        "SCISSORS" {
            superConstructor("peace".lit())
        }
        "PAPER" {
            superConstructor("flat".lit())
        }
    }

    println(
        File("ExampleEleven") {
            type(helloWorld)
        }
    )
}

fun example11Poet() { //Christ this is bad
    val helloWorld = TypeSpec.enumBuilder("Roshambo")
        .primaryConstructor(FunSpec.constructorBuilder()
            .addParameter("handsign", String::class)
            .build())
        .addEnumConstant("ROCK", TypeSpec.anonymousClassBuilder()
            .addSuperclassConstructorParameter("%S", "fist")
            .addFunction(FunSpec.builder("toString")
                .addModifiers(KModifier.OVERRIDE)
                .addStatement("return %S", "avalanche!")
                .returns(String::class)
                .build())
            .build())
        .addEnumConstant("SCISSORS", TypeSpec.anonymousClassBuilder()
            .addSuperclassConstructorParameter("%S", "peace")
            .build())
        .addEnumConstant("PAPER", TypeSpec.anonymousClassBuilder()
            .addSuperclassConstructorParameter("%S", "flat")
            .build())
        .addProperty(PropertySpec.builder("handsign", String::class, KModifier.PRIVATE)
            .initializer("handsign")
            .build())
        .build()

    println(
        File("ExampleEleven") {
            type(helloWorld)
        }
    )
}


fun example10Sonnet() {
    val helloWorld = Interface("HelloWorld") {
        property("buzz", String::class)
        function("beep")
    }

    println(
        File("ExampleTen") {
            type(helloWorld)
        }
    )
}

fun example10Poet() {
    val helloWorld = TypeSpec.interfaceBuilder("HelloWorld")
        .addProperty("buzz", String::class)
        .addFunction(FunSpec.builder("beep")
            .addModifiers(KModifier.ABSTRACT)
            .build())
        .build()

    println(
        File("ExampleTen") {
            type(helloWorld)
        }
    )
}

fun example9Sonnet() {
    val flux = Constructor {
        parameter("greeting", String::class)
        +"this.%N = %N".with("greeting", "greeting")
    }

    val helloWorld = Class("HelloWorld") {
        primaryConstructor(flux)
        property("greeting", String::class) { visibility = Visibility.PRIVATE }
    }

    println(
        File("ExampleNine") {
            type(helloWorld)
        }
    )

}

fun example9Poet() {
    val flux = FunSpec.constructorBuilder()
        .addParameter("greeting", String::class)
        .addStatement("this.%N = %N", "greeting", "greeting")
        .build()

    val helloWorld = TypeSpec.classBuilder("HelloWorld")
        .primaryConstructor(flux)
        .addProperty("greeting", String::class, KModifier.PRIVATE)
        .build()

    println(
        File("ExampleNine") {
            type(helloWorld)
        }
    )
}

fun example8Sonnet() {
    val add = Function("add") {
        parameter("a", Int::class)
        parameter("b", Int::class) {
            defaultValue { +"%L".with(0) }
        }
        +"print(\"a + b = \${ a + b }\")"
    }

    println(
        File("ExampleEight") {
            function(add)
        }
    )
}

fun example8Poet() {
    val add = FunSpec.builder("add")
        .addParameter("a", Int::class)
        .addParameter(ParameterSpec.builder("b", Int::class)
            .defaultValue("%L", 0)
            .build())
        .addStatement("print(\"a + b = \${ a + b }\")")
        .build()

    println(
        File("ExampleEight") {
            function(add)
        }
    )
}

fun example7Sonnet() {
    val square = Function("square", Int::class) {
        receiver(Int::class)
        +"var s = this * this"
        +"return s"
    }

    println(
        File("ExampleSeven") {
            function(square)
        }
    )
}

fun example7Poet() {
    val square = FunSpec.builder("square")
        .receiver(Int::class)
        .returns(Int::class)
        .addStatement("var s = this * this")
        .addStatement("return s")
        .build()

    println(
        File("ExampleSeven") {
            function(square)
        }
    )
}

fun example6Sonnet() {
    val flux = Function("flux") {
        visibility = Visibility.PROTECTED
    }

    val helloWorld = Class("HelloWorld") {
        abstract { function(flux) } //Hmmm.
    }

    println(
        File("ExampleSix") {
            type(helloWorld)
        }
    )
}

fun example6Poet() {
    val flux = FunSpec.builder("flux")
        .addModifiers(KModifier.ABSTRACT, KModifier.PROTECTED)
        .build()

    val helloWorld = TypeSpec.classBuilder("HelloWorld")
        .addModifiers(KModifier.ABSTRACT)
        .addFunction(flux)
        .build()

    println(
        File("ExampleSix") {
            type(helloWorld)
        }
    )
}

fun example5Sonnet() {
    val hoverboard = className("Hoverboard").fromPackage("com.mattel")
    val list = className("List").fromPackage("kotlin.collections")
    val arrayList = className("ArrayList").fromPackage("kotlin.collections")
    val listOfHoverboards = list[hoverboard]
    val arrayListOfHoverboards = arrayList[hoverboard]

    val thing = className("Thing").fromPackage("com.mattel")
    val array = className("Array").fromPackage("kotlin")
    val producerArrayOfThings = array[thing.outVariance()]

    val beyond = Function("beyond", listOfHoverboards) {
        +"val result %T()".with(arrayListOfHoverboards)
        +"result += %T()".with(hoverboard)
        +"result += %T()".with(hoverboard)
        +"result += %T()".with(hoverboard)
        +"return result"
    }

    val printThings = Function("printThings") {
        parameter("things", producerArrayOfThings)
        +"println(things)"
    }

    println(
        File("ExampleFive") {
            function(beyond)
            function(printThings)
        }
    )

}

fun example5Poet() {
    val hoverboard = ClassName("com.mattel", "Hoverboard")
    val list = ClassName("kotlin.collections", "List")
    val arrayList = ClassName("kotlin.collections", "ArrayList")
    val listOfHoverboards = list.parameterizedBy(hoverboard)
    val arrayListOfHoverboards = arrayList.parameterizedBy(hoverboard)

    val thing = ClassName("com.misc", "Thing")
    val array = ClassName("kotlin", "Array")
    val producerArrayOfThings = array.parameterizedBy(WildcardTypeName.producerOf(thing))

    val beyond = FunSpec.builder("beyond")
        .returns(listOfHoverboards)
        .addStatement("val result = %T()", arrayListOfHoverboards)
        .addStatement("result += %T()", hoverboard)
        .addStatement("result += %T()", hoverboard)
        .addStatement("result += %T()", hoverboard)
        .addStatement("return result")
        .build()

    val printThings = FunSpec.builder("printThings")
        .addParameter("things", producerArrayOfThings)
        .addStatement("println(things)")
        .build()

    println(
        File("ExampleFive") {
            function(beyond)
            function(printThings)
        }
    )
}

fun example4Sonnet() {
    fun computeRange(name: String, from: Int, to: Int, op: String): FunSpec =
        Function(name, Int::class) {
            +"var result = 1"
            controlFlow("for (i in $from until $to)") {
                +"result = result $op i"
            }
            +"return result"
        }

    println(
        File("ExampleFour") {
            function(computeRange("multiply10to20", 10, 20, "*"))
        }
    )
}

fun example4Poet() {
    fun computeRange(name: String, from: Int, to: Int, op: String): FunSpec {
        return FunSpec.builder(name)
            .returns(Int::class)
            .addStatement("var result = 1")
            .beginControlFlow("for (i in $from until $to)")
            .addStatement("result = result $op i")
            .endControlFlow()
            .addStatement("return result")
            .build()
    }

    println(
        FileSpec.builder("", "ExampleFour")
            .addFunction(computeRange("multiply10to20", 10, 20, "*"))
            .build()
    )
}

fun example3Sonnet() {
    val java = Property("java", String::class.nullable) {
        mutable = true
        visibility = Visibility.PRIVATE
        initializer { +"null" }
    }
    val helloWorld = Class("HelloWorld") {
        property(java)
        property("kotlin", String::class) { visibility = Visibility.PRIVATE }
    }

    val file = File("Blah") {
        type(helloWorld)
    }

    println(file)

}

fun example3Poet() {
    val java = PropertySpec.builder("java", String::class.asTypeName().copy(nullable = true))
        .mutable()
        .addModifiers(KModifier.PRIVATE)
        .addModifiers(KModifier.PUBLIC) //!?
        .initializer("null")
        .build()

    val helloWorld = TypeSpec.classBuilder("HelloWorld")
        .addProperty(java)
        .addProperty("kotlin", String::class, KModifier.PRIVATE)
        .build()

    val file = FileSpec.builder("", "Blah")
        .addType(helloWorld)
        .build()

    println(file)
}


fun example2Sonnet() {
    val main = Function("main") {
        +"var total = 0"
        controlFlow("for (i in 0 until 10)") {
            +"total += i"
        }
    }

    val file = File("Blah") {
        function(main)
    }

    println(file)
}


fun example2Poet() {
    val main = FunSpec.builder("main")
        .addStatement("var total = 0")
        .beginControlFlow("for (i in 0 until 10)")
        .addStatement("total += i")
        .endControlFlow()
        .build()

    val file = FileSpec.builder("", "Blah")
        .addFunction(main)
        .build()

    println(file)
}


fun example1Sonnet() {
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
}

fun example1Poet() {
    val greeterClass = ClassName("", "Greeter")
    val file = FileSpec.builder("", "HelloWorld")
        .addType(
            TypeSpec.classBuilder("Greeter")
            .primaryConstructor(
                FunSpec.constructorBuilder()
                .addParameter("name", String::class)
                .build())
            .addProperty(
                PropertySpec.builder("name", String::class)
                .initializer("name")
                .build())
            .addFunction(
                FunSpec.builder("greet")
                .addStatement("println(%P)", "Hello, \$name")
                .build())
            .build())
        .addFunction(
            FunSpec.builder("main")
            .addParameter("args", String::class, KModifier.VARARG)
            .addStatement("%T(args[0]).greet()", greeterClass)
            .build())
        .build()

    println(file)
}