package sonnetkt

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

public open class Function protected constructor(name: String) {
    protected open var builder: FunSpec.Builder = FunSpec.builder(name)

    private fun spec(method: FunSpec.Builder.() -> FunSpec.Builder) {
        builder = builder.method()
    }

    private fun build(block: Function.() -> Unit): FunSpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(name: String, returns: TypeName, block: Function.() -> Unit): FunSpec {
            return Function(name)
                .apply { spec { returns(returns) } }
                .build(block)
        }

        public operator fun invoke(name: String, returns: KClass<*>, block: Function.() -> Unit): FunSpec =
            invoke(name, returns.asTypeName(), block)

        public operator fun invoke(name: String, block: Function.() -> Unit): FunSpec {
            return Function(name).build(block)
        }
    }

    public var visibility: Visibility = Visibility.PUBLIC
        set(value) {
            spec {
                apply { modifiers.removeIf { it in Visibility.modifiers } }
                addModifiers(value.modifier)
            }
            field = value
        }

    public var override: Boolean
        get() = KModifier.OVERRIDE in builder.modifiers
        set(value) {
            if (value) {
                spec { addModifiers(KModifier.OVERRIDE) }
            } else {
                builder.modifiers.remove(KModifier.OVERRIDE)
            }
        }

    public var inline: Boolean
        get() = KModifier.INLINE in builder.modifiers
        set(value) {
            if (value) {
                spec { addModifiers(KModifier.INLINE) }
            } else {
                builder.modifiers.remove(KModifier.INLINE)
            }
        }

    public operator fun String.unaryPlus() {
        spec { addStatement(this@unaryPlus) }
    }

    public operator fun Stanza.unaryPlus() {
        spec { addStatement(format, *args) }
    }

    public fun parameter(name: String, type: TypeName, block: Parameter.() -> Unit = {}) {
        spec { addParameter(Parameter(name, type, block)) }
    }

    public fun parameter(name: String, type: KClass<*>, block: Parameter.() -> Unit = {}) {
        parameter(name, type.asTypeName(), block)
    }

    public fun receiver(type: TypeName) {
        spec { receiver(type) }
    }

    public fun receiver(type: KClass<*>) {
        receiver(type.asTypeName())
    }

    public fun controlFlow(headline: Stanza, block: ControlFlowCode.() -> Unit) {
        spec {
            addCode(
                Code { controlFlow(headline, block) }
            )
        }
    }

    public fun controlFlow(headline: String, block: Code.() -> Unit) {
        controlFlow(headline.with(), block)
    }

    public fun annotation(type: ClassName) {
        spec { addAnnotation(type) }
    }

    public fun annotation(type: KClass<*>) {
        annotation(type.asClassName())
    }

    public fun annotation(type: ClassName, block: Annotation.() -> Unit) {
        spec { addAnnotation(Annotation(type, block)) }
    }


}

public class Constructor private constructor() : Function("constructor") {
    override var builder: FunSpec.Builder = FunSpec.constructorBuilder()

    private fun build(block: Constructor.() -> Unit): FunSpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(block: Constructor.() -> Unit): FunSpec {
            return Constructor().build(block)
        }
    }
}

public class Getter private constructor() : Function("get") {
    override var builder: FunSpec.Builder = FunSpec.getterBuilder()

    private fun build(block: Getter.() -> Unit): FunSpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(block: Getter.() -> Unit): FunSpec {
            return Getter().build(block)
        }
    }
}

public class Setter private constructor(private val propertyType: TypeName) : Function("set") {
    override var builder: FunSpec.Builder = FunSpec.setterBuilder()

    private fun build(block: Setter.() -> Unit): FunSpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(type: TypeName, block: Setter.() -> Unit): FunSpec {
            return Setter(type).build(block)
        }
    }

    public fun parameter(name: String) {
        super.parameter(name, propertyType) {}
    }
}



public class Parameter private constructor(name: String, type: TypeName) {
    private var builder = ParameterSpec.builder(name, type)

    private fun spec(method: ParameterSpec.Builder.() -> ParameterSpec.Builder) {
        builder = builder.method()
    }

    private fun build(block: Parameter.() -> Unit): ParameterSpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(name: String, type: TypeName, block: Parameter.() -> Unit): ParameterSpec {
            return Parameter(name, type).build(block)
        }
    }

    public var vararg: Boolean = false
        get() = KModifier.VARARG in builder.modifiers
        set(value) {
            if (value) {
                spec { addModifiers(KModifier.VARARG) }
            } else {
                builder.modifiers.remove(KModifier.VARARG)
            }
            field = value
        }

    public fun defaultValue(block: Code.() -> Unit) {
        spec { defaultValue(Code(block)) }
    }

    public fun defaultValue(value: Stanza) {
        spec { defaultValue(value.format, *value.args) }
    }

    public fun defaultValue(value: String) {
        defaultValue(value.with())
    }
}