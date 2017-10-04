package io.github.ranolp.waffle.packet

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonObject
import io.github.ranolp.waffle.api.Waffle
import io.github.ranolp.waffle.structure.user.Player

class PacketInChat(private val from: Player, private val message: String) : InPacket {
    override val id = 1

    companion object {
        fun fromJson(jsonObject: JsonObject, from: Player): PacketInChat {
            return PacketInChat(from, jsonObject["data"]["message"].string)
        }
    }

    suspend override fun process() {
        Waffle.broadcast(message, from)
    }
}