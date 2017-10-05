package io.github.ranolp.waffle.api

import io.github.ranolp.waffle.chat.Server
import io.github.ranolp.waffle.structure.user.EmptyUser
import io.github.ranolp.waffle.structure.user.User

object Waffle {
    suspend fun broadcast(message: String, from: User = EmptyUser) {
        Server.broadcast(message, from)
    }
}