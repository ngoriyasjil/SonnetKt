package sonnetkt

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import kotlin.reflect.KClass

class File private constructor(name: String, packageName: String = "") {

    private var builder = FileSpec.builder(packageName, name)

    private fun spec(method: FileSpec.Builder.() -> FileSpec.Builder) {
        builder = builder.method()
    }

    private fun build(block: File.() -> Unit): FileSpec {
        apply(block)
        return builder.build()
    }

    companion object {
        operator fun invoke(name: String, packageName: String = "", block: File.() -> Unit): FileSpec {
            return File(name, packageName).build(block)
        }
    }

    fun type(type: TypeSpec) {
        spec { addType(type) }
    }

    fun defineClass(name: String, block: Class.() -> Unit) {
        type(Class(name, block))
    }

    fun function(function: FunSpec) {
        spec { addFunction(function) }
    }

    fun function(name: String, block: Function.() -> Unit) {
        function(Function(name, block))
    }

    fun function(name: String, returns: TypeName, block: Function.() -> Unit) {
        function(Function(name, returns, block))
    }

    fun function(name: String, returns: KClass<*>, block: Function.() -> Unit) {
        function(Function(name, returns, block))
    }
}