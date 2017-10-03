@file:JvmName("Main")

package io.github.ranolp.kttu

import io.github.ranolp.kttu.log.*
import io.github.ranolp.kttu.settings.Setting
import io.github.ranolp.kttu.util.Json
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.ApplicationCallPipeline
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.content.default
import org.jetbrains.ktor.content.file
import org.jetbrains.ktor.content.static
import org.jetbrains.ktor.content.staticRootFolder
import org.jetbrains.ktor.features.AutoHeadResponse
import org.jetbrains.ktor.features.CallLogging
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.pipeline.PipelinePhase
import org.jetbrains.ktor.request.host
import org.jetbrains.ktor.request.httpMethod
import org.jetbrains.ktor.request.httpVersion
import org.jetbrains.ktor.request.uri
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.route
import org.jetbrains.ktor.routing.routing
import java.io.File

fun Application.module() {
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(HttpRequestLogger) {
        logger = Logger::verbose
        format = { uri, method, host, version, status ->
            "$uri \"$method $host HTTP/$version\" " + when {
                status === null -> ""
                status.value / 100 == 2 -> status.toString().color(background = Background.GREEN, text = Text.BLACK)
                else -> status.toString()
            }
        }
    }
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
            }
            default("PageNotFound.html")
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
    Logger.info("Start KTtu server with port ${Setting.INSTANCE.port}")
    Logger.info("Current log level :: ${Logger.logLevel}")
    val host = embeddedServer(Netty, Setting.INSTANCE.port, watchPaths = listOf("Main"), module = Application::module)
    host.start()
}