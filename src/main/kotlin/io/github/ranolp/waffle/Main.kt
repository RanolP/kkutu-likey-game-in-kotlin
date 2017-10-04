@file:JvmName("Main")

package io.github.ranolp.waffle

import io.github.ranolp.waffle.log.*
import io.github.ranolp.waffle.settings.Setting
import io.github.ranolp.waffle.util.Json
import org.jetbrains.ktor.application.Application
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
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.route
import org.jetbrains.ktor.routing.routing
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
                status.value / 100 == 2 -> status.toString().color(background = Background.GREEN, text = Text.BLACK)
                status.value / 100 == 3 -> status.toString().color(background = Background.CYAN, text = Text.BLACK)
                status.value / 100 == 4 || status.value / 100 == 5 -> status.toString().color(background = Background.RED,
                        text = Text.BLACK)
                else -> status.toString()
            }
        }
    }
    install(FreeMarker)
    routing {
        staticRootFolder = File("resources/view/")

        static("/css") {
            static("/Standard.css") {
                default("css/Standard.css")
            }
        }
        static("/lobby") {
            default("Lobby.html")
        }
        route("/{...}") {
            get {
                call.response.status(HttpStatusCode.NotFound)
                call.respondTemplate("resources/view/PageNotFound.ftl", mapOf("request" to URLDecoder.decode(call.request.uri, "UTF-8")))
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