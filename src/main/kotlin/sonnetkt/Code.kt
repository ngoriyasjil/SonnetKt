package sonnetkt

import com.squareup.kotlinpoet.CodeBlock

open class Code {
    protected var builder = CodeBlock.builder()

    protected fun spec(method: CodeBlock.Builder.() -> CodeBlock.Builder) {
        builder = builder.method()
    }

    private fun build(block: Code.() -> Unit): CodeBlock {
        apply(block)
        return builder.build()
    }

    companion object {
        operator fun invoke(block: Code.() -> Unit): CodeBlock {
            return Code().build(block)
        }
    }

    operator fun Stanza.unaryPlus() {
        spec { addStatement(format, *args) }
    }

    operator fun String.unaryPlus() {
        spec { addStatement(this@unaryPlus) }
    }

    fun controlFlow(headline: Stanza, block: ControlFlowCode.() -> Unit) {
        spec {
            beginControlFlow(headline.format, *headline.args)
            add(ControlFlowCode(block))
            endControlFlow()
        }
    }

    fun controlFlow(headline: String, block: Code.() -> Unit) =
        controlFlow(headline.with(), block)

}

class ControlFlowCode: Code() {

    fun build(block: ControlFlowCode.() -> Unit): CodeBlock {
        apply(block)
        return builder.build()
    }

    companion object {
        operator fun invoke(block: ControlFlowCode.() -> Unit): CodeBlock {
            return ControlFlowCode().build(block)
        }
    }

    fun nextControlFlow(line: Stanza) {
        spec { nextControlFlow(line.format, *line.args)}
    }

    fun nextControlFlow(line: String) = nextControlFlow(line.with())
}