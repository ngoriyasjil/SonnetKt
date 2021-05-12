package sonnetkt

import com.squareup.kotlinpoet.KModifier

@Suppress("unused")
enum class Visibility(val modifier: KModifier) {
    PRIVATE(KModifier.PRIVATE),
    PROTECTED(KModifier.PROTECTED),
    INTERNAL(KModifier.INTERNAL),
    PUBLIC(KModifier.PUBLIC);

    companion object {
        val modifiers = setOf(
            KModifier.PRIVATE,
            KModifier.PROTECTED,
            KModifier.INTERNAL,
            KModifier.PUBLIC,
        )
    }
}