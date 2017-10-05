package io.github.ranolp.waffle.structure.user

import io.github.ranolp.waffle.log.Logger
import io.github.ranolp.waffle.packet.OutPacket
import kotlinx.coroutines.experimental.channels.ClosedSendChannelException
import org.jetbrains.ktor.websocket.CloseReason
import org.jetbrains.ktor.websocket.Frame
import org.jetbrains.ktor.websocket.WebSocketSession
import org.jetbrains.ktor.websocket.close
import java.nio.ByteBuffer

class Player(override val id: String) : User {
    internal var session: WebSocketSession? = null
    override var displayName: String = id
    override val locale = "ko"
    override val icon = "/file/image/Unknown.png"
    suspend override fun sendPacket(packet: OutPacket) {
        val session = this.session
        if (session === null) {
            return
        }
        Logger.verbose("Send packet ${packet::class.simpleName}(${packet.id}) to $displayName")
        try {
            session.send(Frame.Text(true, ByteBuffer.wrap(packet.toJson(this).toString().toByteArray())))
        } catch (t: Throwable) {
            Logger.error(t)
            try {
                session.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, ""))
            } catch (ignore: ClosedSendChannelException) {
                // at some point it will get closed
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (other as? User)?.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}