package sonnetkt

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

class Property private constructor(name: String, private val type: TypeName) {
    private var builder = PropertySpec.builder(name, type)

    private fun spec(method: PropertySpec.Builder.() -> PropertySpec.Builder) {
        builder = builder.method()
    }

    private fun build(block: Property.() -> Unit): PropertySpec {
        apply(block)
        return builder.build()
    }

    companion object {
        operator fun invoke(name: String, type: TypeName, block: Property.() -> Unit): PropertySpec {
            return Property(name, type).build(block)
        }

        operator fun invoke(name: String, type: KClass<*>, block: Property.() -> Unit) =
            invoke(name, type.asTypeName(), block)
    }

    var mutable = false
    set(value) {
        spec { mutable(value) }
        field = value
    }

    var visibility = Visibility.PUBLIC
        set(value) {
            spec {
                apply { modifiers.removeIf { it in Visibility.modifiers } }
                addModifiers(value.modifier)
            }
            field = value
        }

    fun initializer(block: Code.() -> Unit) {
        spec { initializer(Code(block)) }
    }

    fun initializer(value: Stanza) {
        spec { initializer(value.format, *value.args) }
    }

    fun initializer(value: String) {
        initializer(value.with())
    }

    fun getter(block: Getter.() -> Unit) {
        spec { getter(Getter(block)) }
    }

    fun setter(block: Setter.() -> Unit) {
        mutable = true
        spec { setter(Setter(type, block)) }
    }


}