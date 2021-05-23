package sonnetkt

import com.squareup.kotlinpoet.CodeBlock

public open class Code {
    protected var builder: CodeBlock.Builder = CodeBlock.builder()

    protected fun spec(method: CodeBlock.Builder.() -> CodeBlock.Builder) {
        builder = builder.method()
    }

    private fun build(block: Code.() -> Unit): CodeBlock {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(block: Code.() -> Unit): CodeBlock {
            return Code().build(block)
        }
    }

    public operator fun Stanza.unaryPlus() {
        spec { addStatement(format, *args) }
    }

    public operator fun String.unaryPlus() {
        spec { addStatement(this@unaryPlus) }
    }

    public fun controlFlow(headline: Stanza, block: ControlFlowCode.() -> Unit) {
        spec {
            beginControlFlow(headline.format, *headline.args)
            add(ControlFlowCode(block))
            endControlFlow()
        }
    }

    public fun controlFlow(headline: String, block: Code.() -> Unit) {
        controlFlow(headline.with(), block)
    }


}

public class ControlFlowCode private constructor(): Code() {

    private fun build(block: ControlFlowCode.() -> Unit): CodeBlock {
        apply(block)
        return builder.build()
    }

    public companion object {
        public operator fun invoke(block: ControlFlowCode.() -> Unit): CodeBlock {
            return ControlFlowCode().build(block)
        }
    }

    public fun midControlFlow(line: Stanza) {
        spec { nextControlFlow(line.format, *line.args)}
    }

    public fun midControlFlow(line: String) {
        midControlFlow(line.with())
    }
}