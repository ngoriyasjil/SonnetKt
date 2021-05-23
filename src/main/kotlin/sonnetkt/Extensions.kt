package sonnetkt

public class Stanza(public val format: String, public vararg val args: Any)

public fun String.with(vararg args: Any): Stanza = Stanza(this, *args)

public fun String.literal(): Stanza = "%S".with(this)

public fun Int.literal(): Stanza = "%L".with(this)