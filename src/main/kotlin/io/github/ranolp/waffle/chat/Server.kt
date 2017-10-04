package io.github.ranolp.waffle.chat

import io.github.ranolp.waffle.log.Logger
import io.github.ranolp.waffle.structure.user.EmptyUser
import io.github.ranolp.waffle.structure.user.Player
import io.github.ranolp.waffle.structure.user.User
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

object Server {
    val users = AtomicInteger()
    val memberNames = ConcurrentHashMap<String, String>()
    val members = ConcurrentHashMap<String, MutableList<Player>>()

    suspend fun broadcast(message: String, from: User = EmptyUser) {
        members.values.forEach {
            it.forEach {
                it.sendMessage(message, from)
            }
        }
    }

    suspend fun joinPlayer(player: Player) {
        val name = memberNames.computeIfAbsent(player.id) { "Guest${users.incrementAndGet()}" }
        player.displayName = name
        val list = members.computeIfAbsent(player.id) { CopyOnWriteArrayList<Player>() }
        list.add(player)

        if (list.size == 1) {
            broadcast("$name join the game")
        }
        Logger.info("Player joined:" + " name : ${player.displayName}")
    }

    suspend fun quitPlayer(player: Player) {
        val sessions = members[player.id]
        sessions?.remove(player)

        if (sessions !== null && sessions.isEmpty()) {
            val name = memberNames[player.id] ?: player.id
            broadcast("$name left the game")
            users.decrementAndGet()
        }
        Logger.info("Player quited:" + " name : ${player.displayName}")
    }
}