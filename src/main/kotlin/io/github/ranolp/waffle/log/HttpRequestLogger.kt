package io.github.ranolp.waffle.log

import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.application.ApplicationCallPipeline
import org.jetbrains.ktor.application.ApplicationFeature
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.request.host
import org.jetbrains.ktor.request.httpMethod
import org.jetbrains.ktor.request.httpVersion
import org.jetbrains.ktor.request.uri
import org.jetbrains.ktor.util.AttributeKey
import java.net.URLDecoder

class HttpRequestLogger(val config: Configuration) {
    class Configuration {
        var format: (String, String, String?, String, HttpStatusCode?) -> String = { uri, method, host, version, status ->
            "$uri \"$method $host $version\" $status"
        }
        var logger: (String) -> Unit = {
            println(it)
        }
        var decodeUrl = true
    }

    fun intercept(call: ApplicationCall) {
        config.logger(config.format(call.request.uri.let {
            if (config.decodeUrl) {
                URLDecoder.decode(it, "UTF-8")
            } else {
                it
            }
        }, call.request.httpMethod.value, call.request.host(), call.request.httpVersion, call.response.status()))
    }

    companion object Feature : ApplicationFeature<Application, Configuration, HttpRequestLogger> {
        override val key: AttributeKey<HttpRequestLogger> = AttributeKey("HttpRequestLogger")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): HttpRequestLogger {
            val config = Configuration().apply(configure)
            val feature = HttpRequestLogger(config)
            pipeline.intercept(ApplicationCallPipeline.Fallback) {
                feature.intercept(call)
            }
            return feature
        }
    }
}