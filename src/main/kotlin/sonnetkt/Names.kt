package sonnetkt

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass

fun className(vararg names: String): ClassName = ClassName("", *names)

fun ClassName.fromPackage(packageName: String): ClassName = ClassName(packageName, simpleNames)

fun MemberName.fromPackage(packageName: String): MemberName = MemberName(packageName, simpleName)

fun ClassName.member(name: String): MemberName = MemberName(this, name)

fun memberName(name: String): MemberName = MemberName("", name)

val TypeName.nullable: TypeName
    get() = copy(nullable = true)

val KClass<*>.nullable: TypeName
    get() = asTypeName().nullable

fun TypeName.outVariance() = WildcardTypeName.producerOf(this)

fun TypeName.inVariance() = WildcardTypeName.consumerOf(this)

operator fun ClassName.get(vararg parameters: TypeName) = parameterizedBy(*parameters)