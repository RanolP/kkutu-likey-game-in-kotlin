package io.github.ranolp.kttu.settings

import com.github.salomonbrys.kotson.byString
import com.google.gson.JsonObject

class LogSetting(json: JsonObject) {
    companion object {
        val INSTANCE: LogSetting
            get() = Setting.INSTANCE.logSettings
    }
    val dateSyntax by json.byString("date-syntax") { "yy-MM-dd hh:mm:ss" }
    val level by json.byString { "info" }
}