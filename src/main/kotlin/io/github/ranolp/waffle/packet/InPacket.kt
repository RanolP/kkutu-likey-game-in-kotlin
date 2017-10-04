package io.github.ranolp.waffle.packet

import com.github.salomonbrys.kotson.int
import com.google.gson.JsonObject
import io.github.ranolp.waffle.structure.user.Player

interface InPacket : Packet {
    // Client to server
    companion object {
        fun decode(jsonObject: JsonObject, from: Player): InPacket? {
            return when (jsonObject["id"].int) {
                1 -> PacketInChat.fromJson(jsonObject, from)
                else -> null
            }
        }
    }
    suspend fun process()
}