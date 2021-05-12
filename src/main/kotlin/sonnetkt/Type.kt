package sonnetkt

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

class Class private constructor(name: String): Type() {

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

}