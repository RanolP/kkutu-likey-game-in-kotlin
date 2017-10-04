package io.github.ranolp.waffle.log

import io.github.ranolp.waffle.settings.LogSetting
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    enum class LogLevel(val level: Int) {
        ALL(Integer.MIN_VALUE),
        VERBOSE(-1),
        INFO(0),
        WARNING(1),
        ERROR(2),
        OFF(Integer.MAX_VALUE)
    }

    private var formatter: DateTimeFormatter? = null
    val logLevel: LogLevel by lazy {
        LogLevel.values().firstOrNull { it.name.toLowerCase() == LogSetting.INSTANCE.level.toLowerCase() } ?: LogLevel.INFO
    }

    private fun log(level: LogLevel, text: String) {
        if (Logger.logLevel.level > level.level) {
            return
        }
        val now = LocalDateTime.now()
        if (formatter === null) {
            formatter = DateTimeFormatter.ofPattern(LogSetting.INSTANCE.dateSyntax)
        }
        println("[${now.format(formatter)}/$level] $text")
    }

    fun verbose(text: String) {
        log(LogLevel.VERBOSE, text)
    }

    fun info(text: String) {
        log(LogLevel.INFO, text)
    }

    fun warning(text: String) {
        log(LogLevel.WARNING, text)
    }

    fun error(text: String) {
        log(LogLevel.ERROR, text)
    }

    fun error(throwable: Throwable) {
        log(LogLevel.ERROR,
                (throwable::class.simpleName + ": " + throwable.message + throwable.stackTrace.joinToString { "\n  at ${it.className}.${it.methodName}" + if (it.isNativeMethod) "(Native Method)" else "(${it.fileName}:${it.lineNumber})" }).color(
                        text = Text.RED))
    }
}