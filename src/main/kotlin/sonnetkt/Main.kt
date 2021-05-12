package sonnetkt

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass

fun className(vararg names: String): ClassName = ClassName("", *names)

fun ClassName.fromPackage(packageName: String): ClassName = ClassName(packageName, simpleNames)

operator fun ClassName.get(vararg parameters: TypeName) =
    parameterizedBy(*parameters)

val KClass<*>.nullable: TypeName
    get() = asTypeName().nullable

fun TypeName.outVariance() = WildcardTypeName.producerOf(this)

fun TypeName.inVariance() = WildcardTypeName.consumerOf(this)

val TypeName.nullable: TypeName
    get() = copy(nullable = true)

class Stanza(val format: String, vararg val args: Any)

fun String.with(vararg args: Any): Stanza = Stanza(this, *args)

fun String.lit() = "%S".with(this)