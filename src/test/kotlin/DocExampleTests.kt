import com.squareup.kotlinpoet.FunSpec
import org.junit.Test
import sonnetkt.*
import sonnetkt.Function
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DocExampleTests {
    @Test
    fun example1() {

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

            return file.toString()
        }

        assertEquals(example1Poet(), example1Sonnet())
        assertEquals(example1Sonnet(), example1Target)
    }

    @Test
    fun example2() {

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
        //Curly brace formatting fuckery means that these aren't equal.
        assertNotEquals(example2Poet(), example2Sonnet())
        assertEquals(example2Poet(), example2Target)
    }


    @Test
    fun example3() {

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
    fun example4() {
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

        //More curly brace formatting fuckery
        assertNotEquals(example4Poet(), example4Sonnet())
        assertEquals(example4Poet(), example4Target)
    }

    @Test
    fun example5() {
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
    fun example6() {
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
    fun example7() {
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
    fun example8() {
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
    fun example9() {
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
    fun example10() {
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
    fun example11() {
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
}