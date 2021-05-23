package sonnetkt

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

public class Property private constructor(name: String, private val type: TypeName) {
    private var builder = PropertySpec.builder(name, type)

    private fun spec(method: PropertySpec.Builder.() -> PropertySpec.Builder) {
        builder = builder.method()
    }

    private fun build(block: Property.() -> Unit): PropertySpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(name: String, type: TypeName, block: Property.() -> Unit): PropertySpec {
            return Property(name, type).build(block)
        }

        public operator fun invoke(name: String, type: KClass<*>, block: Property.() -> Unit): PropertySpec {
            return invoke(name, type.asTypeName(), block)
        }
    }

    public var mutable: Boolean = false
    set(value) {
        spec { mutable(value) }
        field = value
    }

    public var visibility: Visibility = Visibility.PUBLIC
        set(value) {
            spec {
                apply { modifiers.removeIf { it in Visibility.modifiers } }
                addModifiers(value.modifier)
            }
            field = value
        }

    public fun initializer(block: Code.() -> Unit) {
        spec { initializer(Code(block)) }
    }

    public fun initializer(value: Stanza) {
        spec { initializer(value.format, *value.args) }
    }

    public fun initializer(value: String) {
        initializer(value.with())
    }

    public fun getter(block: Getter.() -> Unit) {
        spec { getter(Getter(block)) }
    }

    public fun setter(block: Setter.() -> Unit) {
        mutable = true
        spec { setter(Setter(type, block)) }
    }


}