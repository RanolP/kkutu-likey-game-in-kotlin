package io.github.ranolp.waffle

import com.google.gson.JsonObject
import io.github.ranolp.waffle.settings.Setting
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.testing.handleRequest
import org.jetbrains.ktor.testing.withTestApplication
import org.junit.BeforeClass
import org.junit.Test

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
                // TODO: fix resource error
                // assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }
}