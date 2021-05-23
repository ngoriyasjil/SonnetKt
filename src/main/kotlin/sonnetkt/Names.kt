package sonnetkt

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass

public fun className(vararg names: String): ClassName = ClassName("", *names)

public fun ClassName.fromPackage(packageName: String): ClassName = ClassName(packageName, simpleNames)

public fun MemberName.fromPackage(packageName: String): MemberName = MemberName(packageName, simpleName)

public fun ClassName.member(name: String): MemberName = MemberName(this, name)

public fun memberName(name: String): MemberName = MemberName("", name)

public val TypeName.nullable: TypeName
    get() = copy(nullable = true)

public val KClass<*>.nullable: TypeName
    get() = asTypeName().nullable

public fun TypeName.outVariance(): WildcardTypeName = WildcardTypeName.producerOf(this)

public fun TypeName.inVariance(): WildcardTypeName = WildcardTypeName.consumerOf(this)

public operator fun ClassName.get(vararg parameters: TypeName): ParameterizedTypeName = parameterizedBy(*parameters)