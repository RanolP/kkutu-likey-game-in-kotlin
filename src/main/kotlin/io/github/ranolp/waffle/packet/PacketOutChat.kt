package io.github.ranolp.waffle.packet

import com.github.salomonbrys.kotson.jsonObject
import io.github.ranolp.waffle.structure.user.User
import java.time.Instant
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class PacketOutChat(val from: User, val message: String, val time: Long = System.currentTimeMillis()) : OutPacket {
    override val id: Int = 0
    override fun toJson(to: User) = generate {
        val locale = Locale.forLanguageTag(to.locale)
        jsonObject("from" to from.toJson(),
                "message" to message,
                "time" to DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(locale).format(LocalTime.from(
                        Instant.ofEpochMilli(time).atZone(Calendar.getInstance(locale).timeZone.toZoneId()))))
    }
}