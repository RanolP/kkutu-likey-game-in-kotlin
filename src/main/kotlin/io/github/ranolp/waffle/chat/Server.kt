package io.github.ranolp.waffle.chat

import io.github.ranolp.waffle.packet.PacketOutPlayerModified
import io.github.ranolp.waffle.structure.user.EmptyUser
import io.github.ranolp.waffle.structure.user.Player
import io.github.ranolp.waffle.structure.user.User
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object Server {
    val users = AtomicInteger()
    val members = ConcurrentHashMap<String, Player>()

    suspend fun broadcast(message: String, from: User = EmptyUser) {
        members.values.forEach {
            it.sendMessage(message, from)
        }
    }

    suspend fun joinPlayer(player: Player) {
        val name = "Guest${users.incrementAndGet()}"
        player.displayName = name
        members.values.forEach {
            it.sendPacket(PacketOutPlayerModified(player, "join", users.get()))
            player.sendPacket(PacketOutPlayerModified(it, "join", users.get()))
        }
        player.sendPacket(PacketOutPlayerModified(player, "join", users.get()))
        members[player.id] = player
    }

    suspend fun quitPlayer(player: Player) {
        members.remove(player.id)
        users.decrementAndGet()
        members.values.forEach {
            it.sendPacket(PacketOutPlayerModified(player, "quit", users.get()))
        }
    }
}