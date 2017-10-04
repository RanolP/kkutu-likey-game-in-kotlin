package io.github.ranolp.waffle.packet

import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject
import io.github.ranolp.waffle.structure.user.User

interface OutPacket : Packet {
    // Server to client
    fun toJson(to: User): JsonObject

    fun generate(data: () -> JsonObject): JsonObject {
        return jsonObject("id" to id, "data" to data())
    }
}