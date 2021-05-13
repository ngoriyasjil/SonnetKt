package sonnetkt

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

open class Function protected constructor(name: String) {
    protected open var builder = FunSpec.builder(name)

    private fun spec(method: FunSpec.Builder.() -> FunSpec.Builder) {
        builder = builder.method()
    }

    private fun build(block: Function.() -> Unit): FunSpec {
        apply(block)
        return builder.build()
    }

    companion object {
        operator fun invoke(name: String, returns: TypeName, block: Function.() -> Unit): FunSpec {
            return Function(name)
                .apply {
                    spec { returns(returns) }
                }
                .build(block)
        }

        operator fun invoke(name: String, returns: KClass<*>, block: Function.() -> Unit): FunSpec =
            invoke(name, returns.asTypeName(), block)

        operator fun invoke(name: String, block: Function.() -> Unit) =
            invoke(name, Unit::class.asTypeName(), block)
    }

    var visibility = Visibility.PUBLIC
        set(value) {
            spec {
                apply { modifiers.removeIf { it in Visibility.modifiers } }
                addModifiers(value.modifier)
            }
            field = value
        }

    operator fun String.unaryPlus() {
        spec { addStatement(this@unaryPlus) }
    }

    operator fun Stanza.unaryPlus() {
        spec { addStatement(format, *args) }
    }

    fun parameter(name: String, type: TypeName, block: Parameter.() -> Unit = {}) {
        spec { addParameter(Parameter(name, type, block)) }
    }

    fun parameter(name: String, type: KClass<*>, block: Parameter.() -> Unit = {}) {
        parameter(name, type.asTypeName(), block)
    }

    fun receiver(type: TypeName) {
        spec { receiver(type) }
    }

    fun receiver(type: KClass<*>) {
        receiver(type.asTypeName())
    }

    fun controlFlow(headline: Stanza, block: ControlFlowCode.() -> Unit) {
        spec {
            addCode(
                Code { controlFlow(headline, block) }
            )
        }
    }

    fun controlFlow(headline: String, block: Code.() -> Unit) =
        controlFlow(headline.with(), block)

    fun override() {
        spec { addModifiers(KModifier.OVERRIDE) }
    }
}

class Constructor private constructor() : Function("constructor") {
    override var builder = FunSpec.constructorBuilder()

    private fun build(block: Constructor.() -> Unit): FunSpec {
        apply(block)
        return builder.build()
    }

    companion object {
        operator fun invoke(block: Constructor.() -> Unit): FunSpec {
            return Constructor().build(block)
        }
    }
}

class Parameter private constructor(name: String, type: TypeName) {
    private var builder = ParameterSpec.builder(name, type)

    private fun spec(method: ParameterSpec.Builder.() -> ParameterSpec.Builder) {
        builder = builder.method()
    }

    private fun build(block: Parameter.() -> Unit): ParameterSpec {
        apply(block)
        return builder.build()
    }

    companion object {
        operator fun invoke(name: String, type: TypeName, block: Parameter.() -> Unit): ParameterSpec {
            return Parameter(name, type).build(block)
        }
    }

    var vararg: Boolean = false
        get() = KModifier.VARARG in builder.modifiers
        set(value) {
            if (value) {
                spec { addModifiers(KModifier.VARARG) }
            } else {
                builder.modifiers.remove(KModifier.VARARG)
            }
            field = value
        }

    fun defaultValue(block: Code.() -> Unit) {
        spec { defaultValue(Code(block)) }
    }
}