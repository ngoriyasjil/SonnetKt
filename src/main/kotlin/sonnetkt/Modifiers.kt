package sonnetkt

import com.squareup.kotlinpoet.KModifier

@Suppress("unused")
public enum class Visibility(public val modifier: KModifier) {
    PRIVATE(KModifier.PRIVATE),
    PROTECTED(KModifier.PROTECTED),
    INTERNAL(KModifier.INTERNAL),
    PUBLIC(KModifier.PUBLIC);

    public companion object {
        public val modifiers: Set<KModifier> = setOf(
            KModifier.PRIVATE,
            KModifier.PROTECTED,
            KModifier.INTERNAL,
            KModifier.PUBLIC,
        )
    }
}