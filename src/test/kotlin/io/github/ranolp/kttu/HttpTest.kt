package io.github.ranolp.kttu

import com.google.gson.JsonObject
import io.github.ranolp.kttu.settings.Setting
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.testing.handleRequest
import org.jetbrains.ktor.testing.withTestApplication
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals

class HttpTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun `mock setting`() {
            Setting._INSTANCE = Setting(JsonObject())
        }
    }
    @Test
    fun `test 404 page returns`() {
        withTestApplication(Application::module) {
            with(handleRequest(HttpMethod.Get, "/it/must/be/404")) {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }
}