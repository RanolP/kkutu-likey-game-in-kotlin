@file:JvmName("Main")

package io.github.ranolp.waffle

import io.github.ranolp.waffle.chat.Server
import io.github.ranolp.waffle.log.*
import io.github.ranolp.waffle.packet.InPacket
import io.github.ranolp.waffle.settings.Setting
import io.github.ranolp.waffle.structure.user.Player
import io.github.ranolp.waffle.util.Json
import kotlinx.coroutines.experimental.channels.consumeEach
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.ApplicationCallPipeline
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.content.*
import org.jetbrains.ktor.features.AutoHeadResponse
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.freemarker.FreeMarker
import org.jetbrains.ktor.freemarker.respondTemplate
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.request.uri
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.route
import org.jetbrains.ktor.routing.routing
import org.jetbrains.ktor.sessions.*
import org.jetbrains.ktor.util.nextNonce
import org.jetbrains.ktor.websocket.*
import java.io.File
import java.net.URLDecoder

fun Application.module() {
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(HttpRequestLogger) {
        logger = Logger::verbose
        format = { uri, method, host, version, status ->
            "$uri \"$method $host $version\" " + when {
                status === null -> ""
                status.value / 100 == 1 -> status.toString().color(background = Background.BLUE, text = Text.BLACK)
                status.value / 100 == 2 -> status.toString().color(background = Background.GREEN, text = Text.BLACK)
                status.value / 100 == 3 -> status.toString().color(background = Background.CYAN, text = Text.BLACK)
                status.value / 100 == 4 || status.value / 100 == 5 -> status.toString().color(background = Background.RED,
                        text = Text.BLACK)
                else -> status.toString()
            }
        }
    }
    install(FreeMarker)
    install(WebSockets)
    install(Sessions) {
        cookie<Player>("SESSION")
    }
    routing {
        staticRootFolder = File("resources/view/")

        static("/css") {
            static("/Standard.css") {
                default("css/Standard.css")
            }
        }
        static("/js") {
            static("/main.js") {
                default("js/main.js")
            }
        }

        intercept(ApplicationCallPipeline.Infrastructure) {
            if (call.sessions.get<Player>() !== null) {
                call.sessions.set(Player(nextNonce()))
            }
        }

        static("/lobby") {
            default("Lobby.html")
        }
        webSocket("/ws") {
            val player = call.sessions.get<Player>()
            player?.session = this
            if (player == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                call.respondText { "No session" }
                call.response.status(HttpStatusCode.NonAuthoritativeInformation)
                return@webSocket
            }
            Server.joinPlayer(player)
            player.sendMessage("어서와!")
            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        Json.parse(frame.readText()).let {
                            if (it.isJsonObject) {
                                InPacket.decode(it.asJsonObject, player)?.process()
                            }
                        }
                    }
                }
            } finally {
                Server.quitPlayer(player)
            }
        }
        route("/{...}") {
            get {
                call.response.status(HttpStatusCode.NotFound)
                call.respondTemplate("resources/view/PageNotFound.ftl",
                        mapOf("request" to URLDecoder.decode(call.request.uri, "UTF-8")))
            }
        }
    }
}

fun main(args: Array<String>) {
    val json = Json.parseFile("resources/setting.json")
    if (!json.isJsonObject) {
        println("settings.json must be json object syntax!".color(text = Text.RED))
        System.exit(0)
    }
    Setting._INSTANCE = Setting(json.asJsonObject)
    Logger.info("Start Waffle server with port ${Setting.INSTANCE.port}")
    Logger.info("Current log level :: ${Logger.logLevel}")
    val host = embeddedServer(Netty, Setting.INSTANCE.port, watchPaths = listOf("kttu"), module = Application::module)
    host.start()
}