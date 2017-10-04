package io.github.ranolp.waffle.log

/**
 * https://gist.github.com/dainkaplan/4651352
 * Kotlin porting
 */
enum class TextModifier(code: Int) {
    SANE(0),
    HIGH_INTENSITY(1),
    LOW_INTENSITY(2),
    ITALIC(3),
    UNDERLINE(4),
    BLINK(5),
    RAPID_BLINK(6),
    REVERSE_VIDEO(7),
    INVISIBLE_TEXT(8);

    val string = "\u001B[${code}m"
    override fun toString(): String = string
}

enum class Text(code: Int) {
    BLACK(30),
    RED(31),
    GREEN(32),
    YELLOW(33),
    BLUE(34),
    MAGENTA(35),
    CYAN(36),
    WHITE(37),
    RESET(39);

    val string = "\u001B[${code}m"
    override fun toString(): String = string
}

enum class Background(code: Int) {
    BLACK(40),
    RED(41),
    GREEN(42),
    YELLOW(43),
    BLUE(44),
    MAGENTA(45),
    CYAN(46),
    WHITE(47),
    RESET(49);

    val string = "\u001B[${code}m"
    override fun toString(): String = string
}

fun ansi(message: String, vararg modifier: TextModifier = emptyArray(), text: Text = Text.RESET,
        background: Background = Background.RESET): String {
    return modifier.joinToString { it.string } + (if (text !== Text.RESET) text else "") + (if (background !== Background.RESET) background else "") + message + TextModifier.SANE
}

inline fun String.color(vararg modifier: TextModifier = emptyArray(), text: Text = Text.RESET,
        background: Background = Background.RESET): String {
    return ansi(this, *modifier, text = text, background = background)
}