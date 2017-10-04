package io.github.ranolp.waffle.structure.user

import io.github.ranolp.waffle.packet.OutPacket

object EmptyUser : User {
    override var displayName: String
        get() = ""
        set(value) {}
    override val id: String = ""
    override var locale: String
        get() = "en"
        set(value) {}

    override suspend fun sendPacket(packet: OutPacket) {
        // unable
    }
}