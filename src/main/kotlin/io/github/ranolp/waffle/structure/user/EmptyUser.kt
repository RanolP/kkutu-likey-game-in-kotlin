package io.github.ranolp.waffle.structure.user

import io.github.ranolp.waffle.packet.OutPacket

object EmptyUser : User {
    override val displayName = ""
    override val id = ""
    override val locale = "en"
    override val icon = "/file/image/Unknown.png"

    override suspend fun sendPacket(packet: OutPacket) {
        // unable
    }
}