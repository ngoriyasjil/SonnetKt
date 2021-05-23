package sonnetkt

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

public open class Class protected constructor(name: String): Type() {

    override var builder: TypeSpec.Builder = TypeSpec.classBuilder(name)

    private fun build(block: Class.() -> Unit): TypeSpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(name: String, block: Class.() -> Unit): TypeSpec {
            return Class(name).build(block)
        }
    }

    public fun primaryConstructor(constructor: FunSpec) {
        spec { primaryConstructor(constructor) }
    }

    public fun primaryConstructor(block: Constructor.() -> Unit) {
        primaryConstructor(Constructor(block))
    }

    public fun secondaryConstructor(block: Constructor.() -> Unit) {
        spec { addFunction(Constructor(block)) }
    }

    public fun secondaryConstructor(constructor: FunSpec) { //Identical to function(), but expresses intent.
        function(constructor)
    }

    public fun abstract(block: AbstractBlock.() -> Unit = {}) {
        if (KModifier.ABSTRACT !in builder.modifiers) {
            spec { addModifiers(KModifier.ABSTRACT) }
        }
        AbstractBlock(this).block()
    }

    public fun superConstructor(vararg arguments: Stanza) {
        for (arg in arguments) {
            spec { addSuperclassConstructorParameter(arg.format, *arg.args) }
        }
    }
}

public class AbstractBlock(private val parent: Class) {
    public fun function(function: FunSpec) {
        parent.function(
            function
                .toBuilder()
                .addModifiers(KModifier.ABSTRACT)
                .build()
        )
    }

    public fun function(name: String, block: Function.() -> Unit) {
        function(Function(name, block))
    }
}

public class Interface private constructor(name: String): Type() { //generalize over TypeSpec

    override var builder: TypeSpec.Builder = TypeSpec.interfaceBuilder(name)

    private fun build(block: Interface.() -> Unit): TypeSpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(name: String, block: Interface.() -> Unit): TypeSpec {
            return Interface(name).build(block)
        }
    }

    override fun function(function: FunSpec) {

        spec {
            addFunction(
                function.run {
                    if (function.body.isEmpty()) {
                        toBuilder().addModifiers(KModifier.ABSTRACT).build()
                    } else this
                }
            )
        }
    }

}

public abstract class Type {

    protected abstract var builder: TypeSpec.Builder

    protected fun spec(method: TypeSpec.Builder.() -> TypeSpec.Builder) {
        builder = builder.method()
    }

    public fun property(property: PropertySpec) {
        spec { addProperty(property) }
    }

    public fun property(name: String, type: KClass<*>, block: Property.() -> Unit = {}) {
        property(Property(name, type, block))
    }

    public fun property(name: String, type: TypeName, block: Property.() -> Unit = {}) {
        property(Property(name, type, block))
    }

    public open fun function(function: FunSpec) {
        spec { addFunction(function) }
    }

    public fun function(name: String, block: Function.() -> Unit = {}) {
        function(Function(name, block))
    }

    public fun function(name: String, returns: TypeName, block: Function.() -> Unit = {}) {
        function(Function(name, returns, block))
    }

    public fun function(name: String, returns: KClass<*>, block: Function.() -> Unit = {}) {
        function(Function(name, returns, block))
    }

    public fun implements(inter: TypeName) {
        spec { addSuperinterface(inter) }
    }

    public fun implements(inter: KClass<*>) {
        implements(inter.asTypeName())
    }

    public fun implementsBy(inter: TypeName, delegateBlock: Code.() -> Unit) {
        spec { addSuperinterface(inter, Code(delegateBlock)) }
    }

    public fun implementsBy(inter: KClass<*>, delegateBlock: Code.() -> Unit) {
        implementsBy(inter.asTypeName(), delegateBlock)
    }

}

public class EnumClass(name: String) : Class(name) {
    override var builder: TypeSpec.Builder = TypeSpec.enumBuilder(name)

    private fun build(block: EnumClass.() -> Unit): TypeSpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(name: String, block: EnumClass.() -> Unit): TypeSpec {
            return EnumClass(name).build(block)
        }
    }

    public operator fun String.invoke(vararg constructorArguments: Stanza, block: AnonymousClass.() -> Unit = {}) {
        enumConstant(this, *constructorArguments) { block() }
    }

    public fun enumConstant(name: String, vararg constructorArguments: Stanza, block: AnonymousClass.() -> Unit = {}) {
        val fullBlock: AnonymousClass.() -> Unit = {
            apply { superConstructor(*constructorArguments) }
            block()
        }
        spec { addEnumConstant(name, AnonymousClass(fullBlock)) }
    }
}

public class AnonymousClass : Class("<anonymous>") {
    override var builder: TypeSpec.Builder = TypeSpec.anonymousClassBuilder()

    private fun build(block: AnonymousClass.() -> Unit): TypeSpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(block: AnonymousClass.() -> Unit): TypeSpec {
            return AnonymousClass().build(block)
        }
    }
}

public class Object private constructor(name: String) : Class(name) {
    override var builder: TypeSpec.Builder = TypeSpec.objectBuilder(name)

    private fun build(block: Object.() -> Unit): TypeSpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(name: String, block: Object.() -> Unit): TypeSpec {
            return Object(name).build(block)
        }
    }
}

public class Annotation private constructor(type: ClassName) {
    private var builder = AnnotationSpec.builder(type)

    private fun spec(method: AnnotationSpec.Builder.() -> AnnotationSpec.Builder) {
        builder = builder.method()
    }

    private fun build(block: Annotation.() -> Unit): AnnotationSpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(name: ClassName, block: Annotation.() -> Unit): AnnotationSpec {
            return Annotation(name).build(block)
        }
    }

    public fun argument(name: String, value: Stanza) {
        //Dangerous if name contains format strings! Add a check?
        spec { addMember("$name = ${value.format}", *value.args) }
    }

    public fun argument(value: Stanza) {
        spec { addMember(value.format, *value.args) }
    }
}