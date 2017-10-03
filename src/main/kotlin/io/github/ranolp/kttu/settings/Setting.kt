package io.github.ranolp.kttu.settings

import com.github.salomonbrys.kotson.byInt
import com.google.gson.JsonObject
import io.github.ranolp.kttu.util.byClass


class Setting(json: JsonObject) {
    companion object {
        val INSTANCE: Setting
            get() = _INSTANCE
        //
        internal lateinit var _INSTANCE: Setting
    }
    val port by json.byInt("server-port") { 8080 }
    val logSettings by json.byClass(init = ::LogSetting)
}