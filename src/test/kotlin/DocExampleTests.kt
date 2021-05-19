import com.squareup.kotlinpoet.FunSpec
import org.junit.Test
import sonnetkt.*
import kotlin.test.assertEquals

public class DocExampleTests {
    @Test
    public fun example1() {

        val example1Target = """
            import kotlin.String
            import kotlin.Unit
            
            public class Greeter(
              public val name: String
            ) {
              public fun greet(): Unit {
                println(""${'"'}Hello, ${'$'}name""${'"'})
              }
            }
            
            public fun main(vararg args: String): Unit {
              Greeter(args[0]).greet()
            }
            
        """.trimIndent()

        fun example1Sonnet(): String {
            val greeterClass = className("Greeter")
            val file = File("HelloWorld") {
                defineClass("Greeter") {
                    primaryConstructor {
                        parameter("name", String::class)
                    }

                    property("name", String::class) {
                        initializer("name")
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

            return file.toString()
        }

        assertEquals(example1Poet(), example1Sonnet())
        assertEquals(example1Sonnet(), example1Target)
    }

    @Test
    public fun example2() {

        val example2Target = """
            public fun main(): kotlin.Unit {
              var total = 0
              for (i in 0 until 10) {
                total += i
              }
            }
            
        """.trimIndent()

        fun example2Sonnet(): String {
            val main = Function("main") {
                +"var total = 0"
                controlFlow("for (i in 0 until 10)") {
                    +"total += i"
                }
            }

            return main.toString()
        }
        assertEquals(example2Poet(), example2Sonnet())
        assertEquals(example2Poet(), example2Target)
    }


    @Test
    public fun example3() {

        val example3Target = """
            public class HelloWorld {
              private var java: kotlin.String? = null

              private val kotlin: kotlin.String
            }

        """.trimIndent()

        fun example3Sonnet(): String {
            val java = Property("java", String::class.nullable) {
                mutable = true
                visibility = Visibility.PRIVATE
                initializer("null")
            }
            val helloWorld = Class("HelloWorld") {
                property(java)
                property("kotlin", String::class) { visibility = Visibility.PRIVATE }
            }

            return helloWorld.toString()
        }

        assertEquals(example3Poet(), example3Sonnet())
        assertEquals(example3Poet(), example3Target)
    }

    @Test
    public fun example4() {
        val example4Target = """
            public fun multiply10to20(): kotlin.Int {
              var result = 1
              for (i in 10 until 20) {
                result = result * i
              }
              return result
            }

        """.trimIndent()

        fun example4Sonnet(): String {
            fun computeRange(name: String, from: Int, to: Int, op: String): FunSpec =
                Function(name, Int::class) {
                    +"var result = 1"
                    controlFlow("for (i in $from until $to)") {
                        +"result = result $op i"
                    }
                    +"return result"
                }

            return computeRange("multiply10to20", 10, 20, "*").toString()
        }

        assertEquals(example4Poet(), example4Sonnet())
        assertEquals(example4Poet(), example4Target)
    }

    @Test
    public fun example5() {
        val example5Target = """
            import com.mattel.Hoverboard
            import com.misc.Thing
            import kotlin.Array
            import kotlin.Unit
            import kotlin.collections.ArrayList
            import kotlin.collections.List

            public fun beyond(): List<Hoverboard> {
              val result = ArrayList<Hoverboard>()
              result += Hoverboard()
              result += Hoverboard()
              result += Hoverboard()
              return result
            }

            public fun printThings(things: Array<out Thing>): Unit {
              println(things)
            }

        """.trimIndent()

        fun example5Sonnet(): String {
            val hoverboard = className("Hoverboard").fromPackage("com.mattel")
            val list = className("List").fromPackage("kotlin.collections")
            val arrayList = className("ArrayList").fromPackage("kotlin.collections")
            val listOfHoverboards = list[hoverboard]
            val arrayListOfHoverboards = arrayList[hoverboard]

            val thing = className("Thing").fromPackage("com.misc")
            val array = className("Array").fromPackage("kotlin")
            val producerArrayOfThings = array[thing.outVariance()]

            val beyond = Function("beyond", listOfHoverboards) {
                +"val result = %T()".with(arrayListOfHoverboards)
                +"result += %T()".with(hoverboard)
                +"result += %T()".with(hoverboard)
                +"result += %T()".with(hoverboard)
                +"return result"
            }

            val printThings = Function("printThings") {
                parameter("things", producerArrayOfThings)
                +"println(things)"
            }

            return File("ExampleFive") {
                function(beyond)
                function(printThings)
            }.toString()
        }

        assertEquals(example5Poet(), example5Sonnet())
        assertEquals(example5Poet(), example5Target)

    }


    @Test
    public fun example6() {
        val example6Target = """
            public abstract class HelloWorld {
              protected abstract fun flux(): kotlin.Unit
            }

        """.trimIndent()

        fun example6Sonnet(): String {
            val flux = Function("flux") {
                visibility = Visibility.PROTECTED
            }

            val helloWorld = Class("HelloWorld") {
                abstract { function(flux) } //Hmmm.
            }

            return helloWorld.toString()
        }

        assertEquals(example6Poet(), example6Sonnet())
        assertEquals(example6Poet(), example6Target)
    }

    @Test
    public fun example7() {
        val example7Target = """
            public fun kotlin.Int.square(): kotlin.Int {
              var s = this * this
              return s
            }

        """.trimIndent()

        fun example7Sonnet(): String {
            val square = Function("square", Int::class) {
                receiver(Int::class)
                +"var s = this * this"
                +"return s"
            }

            return square.toString()
        }

        assertEquals(example7Poet(), example7Sonnet())
        assertEquals(example7Poet(), example7Target)
    }

    @Test
    public fun example8() {
        val example8Target = """
            public fun add(a: kotlin.Int, b: kotlin.Int = 0): kotlin.Unit {
              print("a + b = ${'$'}{ a + b }")
            }

        """.trimIndent()

        fun example8Sonnet(): String {
            val add = Function("add") {
                parameter("a", Int::class)
                parameter("b", Int::class) {
                    defaultValue(0.literal())
                }
                +"print(\"a + b = \${ a + b }\")"
            }

            return add.toString()
        }

        assertEquals(example8Poet(), example8Sonnet())
        assertEquals(example8Poet(), example8Target)
    }

    @Test
    public fun example9() {
        val example9Target = """
            public class HelloWorld(
              greeting: kotlin.String
            ) {
              private val greeting: kotlin.String
              init {
                this.greeting = greeting
              }
            }

        """.trimIndent()

        fun example9Sonnet(): String {
            val flux = Constructor {
                parameter("greeting", String::class)
                +"this.%N = %N".with("greeting", "greeting")
            }

            val helloWorld = Class("HelloWorld") {
                primaryConstructor(flux)
                property("greeting", String::class) { visibility = Visibility.PRIVATE }
            }

            return helloWorld.toString()
        }
        assertEquals(example9Poet(), example9Sonnet())
        assertEquals(example9Poet(), example9Target)
    }

    @Test
    public fun example10() {
        val example10Target = """
            public interface HelloWorld {
              public val buzz: kotlin.String

              public fun beep(): kotlin.Unit
            }
            
        """.trimIndent()

        fun example10Sonnet(): String {
            val helloWorld = Interface("HelloWorld") {
                property("buzz", String::class)
                function("beep")
            }

            return helloWorld.toString()
        }

        assertEquals(example10Poet(), example10Sonnet())
        assertEquals(example10Poet(), example10Target)
    }

    @Test
    public fun example11() {
        val example11Target = """
            public enum class Roshambo(
              private val handsign: kotlin.String
            ) {
              ROCK("fist") {
                public override fun toString(): kotlin.String = "avalanche!"
              },
              SCISSORS("peace"),
              PAPER("flat"),
              ;
            }
            
        """.trimIndent()

        fun example11Sonnet(): String {
            val helloWorld = EnumClass("Roshambo") {
                primaryConstructor {
                    parameter("handsign", String::class)
                }

                "ROCK"("fist".literal()) {
                    function("toString", String::class) {
                        override = true
                        +"return %S".with("avalanche!")
                    }
                }

                "SCISSORS"("peace".literal())

                "PAPER"("flat".literal())

                property("handsign", String::class) {
                    visibility = Visibility.PRIVATE
                    initializer("handsign")
                }
            }

            return helloWorld.toString()
        }

        assertEquals(example11Poet(), example11Sonnet())
        assertEquals(example11Poet(), example11Target)
    }

    @Test
    public fun example12() {
        val example12Target = """
            var android: kotlin.String
              inline get() = "foo"
              set(`value`) {
              }

        """.trimIndent()

        fun example12Sonnet(): String {
            val android = Property("android", String::class) {
                getter {
                    inline = true
                    +"return %S".with("foo")
                }
                setter {
                    parameter("value") // The setter parameter type is automatically the same as the property type
                }
            }

            return android.toString()
        }

        assertEquals(example12Poet(), example12Sonnet())
        assertEquals(example12Poet(), example12Target)
    }

    @Test
    public fun example13() {
        val example13Target = """
            inline var android: kotlin.String
              get() = "foo"
              set(`value`) {
              }

        """.trimIndent()

        fun example13Sonnet(): String {
            val android = Property("android", String::class) {
                getter {
                    inline = true
                    +"return %S".with("foo")
                }
                setter {
                    inline = true
                    parameter("value")
                }
            }

            return android.toString()
        }

        assertEquals(example13Poet(), example13Sonnet())
        assertEquals(example13Poet(), example13Target)
    }

    @Test
    public fun example14() {
        val example14Target = """
            package com.squareup.example

            import com.squareup.tacos.createTaco
            import com.squareup.tacos.isVegan
            import kotlin.Unit

            public fun main(): Unit {
              val taco = createTaco()
              println(taco.isVegan)
            }

        """.trimIndent()

        fun example14Sonnet(): String {
            val createTaco = className("tacos")
                .fromPackage("com.squareup")
                .member("createTaco")
            val isVegan = className("tacos")
                .fromPackage("com.squareup")
                .member("isVegan")

            val file = File("TacoTest",
                packageName = "com.squareup.example") {
                function("main") {
                    +"val taco = %M()".with(createTaco)
                    +"println(taco.%M)".with(isVegan)
                }
            }

            return file.toString()
        }

        assertEquals(example14Poet(), example14Sonnet())
        assertEquals(example14Poet(), example14Target)
    }

    @Test
    public fun example15() {
        val example15Target = """
            package com.squareup.example

            import com.squareup.cakes.createCake
            import com.squareup.tacos.createTaco
            import kotlin.Unit
            import com.squareup.cakes.isVegan as isCakeVegan
            import com.squareup.tacos.isVegan as isTacoVegan

            public fun main(): Unit {
              val taco = createTaco()
              val cake = createCake()
              println(taco.isTacoVegan)
              println(cake.isCakeVegan)
            }

        """.trimIndent()

        fun example15Sonnet(): String {
            val createTaco = memberName("createTaco")
                .fromPackage("com.squareup.tacos")
            val createCake = memberName("createCake")
                .fromPackage("com.squareup.cakes")
            val isTacoVegan = memberName("isVegan")
                .fromPackage("com.squareup.tacos")
            val isCakeVegan = memberName("isVegan")
                .fromPackage("com.squareup.cakes")

            val file = File("Test",
                packageName = "com.squareup.example") {
                importAlias(isTacoVegan, "isTacoVegan")
                importAlias(isCakeVegan, "isCakeVegan")
                function("main") {
                    +"val taco = %M()".with(createTaco)
                    +"val cake = %M()".with(createCake)
                    +"println(taco.%M)".with(isTacoVegan)
                    +"println(cake.%M)".with(isCakeVegan)
                }
            }

            return file.toString()
        }

        assertEquals(example15Poet(), example15Sonnet())
        assertEquals(example15Poet(), example15Target)
    }

    @Test
    public fun example16() {
        val example16Target = """
            import kotlin.Char
            import kotlin.Int
            import kotlin.String

            public fun hexDigit(i: Int): Char = (if (i < 10) i + '0'.toInt() else i - 10 + 'a'.toInt()).toChar()

            public fun byteToHex(b: Int): String {
              val result = CharArray(2)
              result[0] = hexDigit((b ushr 4) and 0xf)
              result[1] = hexDigit(b and 0xf)
              return String(result)
            }

        """.trimIndent()

        fun example16Sonnet(): String {
            val hexDigit = Function("hexDigit", Char::class) {
                parameter("i", Int::class)
                +"return (if (i < 10) i + '0'.toInt() else i - 10 + 'a'.toInt()).toChar()"
            }
            val byteToHex = Function("byteToHex", String::class) {
                parameter("b", Int::class)
                +"val result = CharArray(2)"
                +"result[0] = %N((b ushr 4) and 0xf)".with(hexDigit)
                +"result[1] = %N(b and 0xf)".with(hexDigit)
                +"return String(result)"
            }

            val file = File("Example16") {
                function(hexDigit)
                function(byteToHex)
            }

            return file.toString()
        }

        assertEquals(example16Poet(), example16Sonnet())
        assertEquals(example16Poet(), example16Target)
    }

    @Test
    public fun example17() {
        val example17Target = """
            public object HelloWorld {
              public val buzz: kotlin.String = "buzz"

              public fun beep(): kotlin.Unit {
                println("Beep!")
              }
            }

        """.trimIndent()

        fun example17Sonnet(): String {
            val helloWorld = Object("HelloWorld") {
                property("buzz", String::class) {
                    initializer("buzz".literal())
                }
                function("beep") {
                    +"println(%S)".with("Beep!")
                }
            }

            return helloWorld.toString()
        }

        assertEquals(example17Poet(), example17Sonnet())
        assertEquals(example17Poet(), example17Target)
    }

    @Test
    public fun example18() {
        val example18Target = """
            @org.junit.Test
            public fun `test string equality`(): kotlin.Unit {
              assertThat("foo").isEqualTo("foo")
            }

        """.trimIndent()

        fun example18Sonnet(): String {
            val test = Function("test string equality") {
                annotation(Test::class)
                +"assertThat(%1S).isEqualTo(%1S)".with("foo")
            }
            return test.toString()
        }

        assertEquals(example18Poet(), example18Sonnet())
        assertEquals(example18Poet(), example18Target)
    }

    @Test
    public fun example19() {
        val example19Target = """
            @HeaderList([
              Header(name = "Accept", value = "application/json; charset=utf-8"),
              Header(name = "User-Agent", value = "Square Cash")
            ])
            public fun recordEvent(logRecord: LogRecord): LogReceipt {
            }

        """.trimIndent()

        fun example19Sonnet(): String {
            val logRecordName = className("LogRecord")
            val logReceipt = className("LogReceipt")
            val headerList = className("HeaderList")
            val header = className("Header")

            val logRecord = Function("recordEvent", logReceipt) {
                annotation(headerList) {
                    argument(
                        "[\n⇥%L,\n%L⇤\n]".with( //What even is this?
                            Annotation(header) {
                                argument("name", "Accept".literal())
                                argument("value", "application/json; charset=utf-8".literal())
                            },
                            Annotation(header) {
                                argument("name", "User-Agent".literal())
                                argument("value", "Square Cash".literal())
                            }
                        )
                    )
                }
                parameter("logRecord", logRecordName)
            }

            return logRecord.toString()
        }

        assertEquals(example19Poet(), example19Sonnet())
        assertEquals(example19Poet(), example19Target)
    }
}

/*
    @Test
    fun exampleX() {
        val exampleXTarget = """

        """.trimIndent()

        fun exampleXSonnet(): String {
            return ""
        }

        assertEquals(exampleXPoet(), exampleXSonnet())
        assertEquals(exampleXPoet(), exampleXTarget)
    }
 */