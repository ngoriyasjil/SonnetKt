package sonnetkt

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

open class Class protected constructor(name: String): Type() {

    override var builder = TypeSpec.classBuilder(name)

    private fun build(block: Class.() -> Unit): TypeSpec {
        apply(block)
        return builder.build()
    }

    companion object {
        operator fun invoke(name: String, block: Class.() -> Unit): TypeSpec {
            return Class(name).build(block)
        }
    }

    fun primaryConstructor(constructor: FunSpec) {
        spec { primaryConstructor(constructor) }
    }

    fun primaryConstructor(block: Constructor.() -> Unit) {
        primaryConstructor(Constructor(block))
    }

    fun abstract(block: AbstractBlock.() -> Unit = {}) {
        if (KModifier.ABSTRACT !in builder.modifiers) {
            spec { addModifiers(KModifier.ABSTRACT) }
        }
        AbstractBlock(this).block()
    }

    fun superConstructor(vararg arguments: Stanza) {
        for (arg in arguments) {
            spec { addSuperclassConstructorParameter(arg.format, *arg.args) }
        }
    }
}

class AbstractBlock(private val parent: Class) {
    fun function(function: FunSpec) {
        parent.function(
            function
                .toBuilder()
                .addModifiers(KModifier.ABSTRACT)
                .build()
        )
    }

    fun function(name: String, block: Function.() -> Unit) {
        function(Function(name, block))
    }
}

class Interface private constructor(name: String): Type() { //generalize over TypeSpec

    override var builder = TypeSpec.interfaceBuilder(name)

    private fun build(block: Interface.() -> Unit): TypeSpec {
        apply(block)
        return builder.build()
    }

    companion object {
        operator fun invoke(name: String, block: Interface.() -> Unit): TypeSpec {
            return Interface(name).build(block)
        }
    }

    override fun function(function: FunSpec) {
        spec {
            addFunction(
                function
                    .toBuilder()
                    .addModifiers(KModifier.ABSTRACT)
                    .build()
            )
        }
    }

}

abstract class Type {

    protected abstract var builder: TypeSpec.Builder

    protected fun spec(method: TypeSpec.Builder.() -> TypeSpec.Builder) {
        builder = builder.method()
    }

    fun property(property: PropertySpec) {
        spec { addProperty(property) }
    }

    fun property(name: String, type: KClass<*>, block: Property.() -> Unit = {}) {
        property(Property(name, type, block))
    }

    fun property(name: String, type: TypeName, block: Property.() -> Unit = {}) {
        property(Property(name, type, block))
    }

    open fun function(function: FunSpec) {
        spec { addFunction(function) }
    }

    fun function(name: String, block: Function.() -> Unit = {}) {
        function(Function(name, block))
    }

    fun function(name: String, returns: TypeName, block: Function.() -> Unit = {}) {
        function(Function(name, returns, block))
    }

    fun function(name: String, returns: KClass<*>, block: Function.() -> Unit = {}) {
        function(Function(name, returns, block))
    }

}

class EnumClass(name: String) : Class(name) {
    override var builder = TypeSpec.enumBuilder(name)

    private fun build(block: EnumClass.() -> Unit): TypeSpec {
        apply(block)
        return builder.build()
    }

    companion object {
        operator fun invoke(name: String, block: EnumClass.() -> Unit): TypeSpec {
            return EnumClass(name).build(block)
        }
    }

    operator fun String.invoke(vararg constructorArguments: Stanza, block: AnonymousClass.() -> Unit = {}) {
        enumConstant(this, *constructorArguments) { block() }
    }

    fun enumConstant(name: String, vararg constructorArguments: Stanza, block: AnonymousClass.() -> Unit = {}) {
        val fullBlock: AnonymousClass.() -> Unit = {
            apply { superConstructor(*constructorArguments) }
            block()
        }
        spec { addEnumConstant(name, AnonymousClass(fullBlock)) }
    }
}

class AnonymousClass : Class("<anonymous>") {
    override var builder = TypeSpec.anonymousClassBuilder()

    private fun build(block: AnonymousClass.() -> Unit): TypeSpec {
        apply(block)
        return builder.build()
    }

    companion object {
        operator fun invoke(block: AnonymousClass.() -> Unit): TypeSpec {
            return AnonymousClass().build(block)
        }
    }
}