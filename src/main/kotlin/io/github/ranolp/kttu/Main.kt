@file:JvmName("Main")

package io.github.ranolp.kttu

import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.CallLogging
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.netty.Netty

fun Application.module() {
    install(CallLogging)
}

fun main(args: Array<String>) {
    val host = embeddedServer(Netty, 8080, watchPaths = listOf("Main"), module = Application::module)
    host.start()
}