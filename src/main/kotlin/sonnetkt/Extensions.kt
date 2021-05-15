package sonnetkt

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass

fun className(vararg names: String): ClassName = ClassName("", *names)

fun ClassName.fromPackage(packageName: String): ClassName = ClassName(packageName, simpleNames)

operator fun ClassName.get(vararg parameters: TypeName) = parameterizedBy(*parameters)

val TypeName.nullable: TypeName
    get() = copy(nullable = true)

val KClass<*>.nullable: TypeName
    get() = asTypeName().nullable

fun TypeName.outVariance() = WildcardTypeName.producerOf(this)

fun TypeName.inVariance() = WildcardTypeName.consumerOf(this)

class Stanza(val format: String, vararg val args: Any)

fun String.with(vararg args: Any): Stanza = Stanza(this, *args)

fun String.literal() = "%S".with(this)

fun Int.literal() = "%L".with(this)