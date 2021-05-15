import sonnetkt.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

fun example1Poet(): String {
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

    return file.toString()
}

fun example2Poet(): String {
    val main = FunSpec.builder("main")
        .addStatement("var total = 0")
        .beginControlFlow("for (i in 0 until 10)")
        .addStatement("total += i")
        .endControlFlow()
        .build()

    return main.toString()
}

fun example3Poet(): String {
    val java = PropertySpec.builder("java", String::class.asTypeName().copy(nullable = true))
        .mutable()
        .addModifiers(KModifier.PRIVATE)
        .initializer("null")
        .build()

    val helloWorld = TypeSpec.classBuilder("HelloWorld")
        .addProperty(java)
        .addProperty("kotlin", String::class, KModifier.PRIVATE)
        .build()

    return helloWorld.toString()
}

fun example4Poet(): String {
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

    return computeRange("multiply10to20", 10, 20, "*").toString()
}

fun example5Poet(): String {
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

    return File("ExampleFive") {
        function(beyond)
        function(printThings)
    }.toString()
}

fun example6Poet(): String {
    val flux = FunSpec.builder("flux")
        .addModifiers(KModifier.ABSTRACT, KModifier.PROTECTED)
        .build()

    val helloWorld = TypeSpec.classBuilder("HelloWorld")
        .addModifiers(KModifier.ABSTRACT)
        .addFunction(flux)
        .build()

    return helloWorld.toString()
}

fun example7Poet(): String {
    val square = FunSpec.builder("square")
        .receiver(Int::class)
        .returns(Int::class)
        .addStatement("var s = this * this")
        .addStatement("return s")
        .build()

    return square.toString()
}

fun example8Poet(): String {
    val add = FunSpec.builder("add")
        .addParameter("a", Int::class)
        .addParameter(ParameterSpec.builder("b", Int::class)
            .defaultValue("%L", 0)
            .build())
        .addStatement("print(\"a + b = \${ a + b }\")")
        .build()

    return add.toString()
}

fun example9Poet(): String {
    val flux = FunSpec.constructorBuilder()
        .addParameter("greeting", String::class)
        .addStatement("this.%N = %N", "greeting", "greeting")
        .build()

    val helloWorld = TypeSpec.classBuilder("HelloWorld")
        .primaryConstructor(flux)
        .addProperty("greeting", String::class, KModifier.PRIVATE)
        .build()

    return helloWorld.toString()
}

fun example10Poet(): String {
    val helloWorld = TypeSpec.interfaceBuilder("HelloWorld")
        .addProperty("buzz", String::class)
        .addFunction(FunSpec.builder("beep")
            .addModifiers(KModifier.ABSTRACT)
            .build())
        .build()

    return helloWorld.toString()
}

fun example11Poet(): String { //Christ this is bad
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

    return helloWorld.toString()
}

