package sonnetkt

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

public class File private constructor(name: String, packageName: String = "") {

    private var builder = FileSpec.builder(packageName, name)

    private fun spec(method: FileSpec.Builder.() -> FileSpec.Builder) {
        builder = builder.method()
    }

    private fun build(block: File.() -> Unit): FileSpec {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(name: String, packageName: String = "", block: File.() -> Unit): FileSpec {
            return File(name, packageName).build(block)
        }
    }

    public fun type(type: TypeSpec) {
        spec { addType(type) }
    }

    public fun defineClass(name: String, block: Class.() -> Unit) {
        type(Class(name, block))
    }

    public fun function(function: FunSpec) {
        spec { addFunction(function) }
    }

    public fun function(name: String, block: Function.() -> Unit) {
        function(Function(name, block))
    }

    public fun function(name: String, returns: TypeName, block: Function.() -> Unit) {
        function(Function(name, returns, block))
    }

    public fun function(name: String, returns: KClass<*>, block: Function.() -> Unit) {
        function(Function(name, returns, block))
    }

    public fun importAlias(member: MemberName, alias: String) {
        spec { addAliasedImport(member, alias) }
    }

    public fun importAlias(type: ClassName, alias: String) {
        spec { addAliasedImport(type, alias) }
    }

    public fun importAlias(type: KClass<*>, alias: String) {
        spec { addAliasedImport(type, alias) }
    }
}