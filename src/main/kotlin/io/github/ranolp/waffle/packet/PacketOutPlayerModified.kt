package io.github.ranolp.waffle.packet

import com.github.salomonbrys.kotson.jsonObject
import io.github.ranolp.waffle.structure.user.Player
import io.github.ranolp.waffle.structure.user.User

class PacketOutPlayerModified(val player: Player, val modify: String, val users: Int) : OutPacket{
    override val id = 2

    override fun toJson(to: User) = generate {
        jsonObject("player" to player.toJson(), "modify" to modify, "users" to users)
    }
}