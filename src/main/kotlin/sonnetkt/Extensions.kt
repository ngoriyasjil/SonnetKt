package sonnetkt

class Stanza(val format: String, vararg val args: Any)

fun String.with(vararg args: Any): Stanza = Stanza(this, *args)

fun String.literal() = "%S".with(this)

fun Int.literal() = "%L".with(this)