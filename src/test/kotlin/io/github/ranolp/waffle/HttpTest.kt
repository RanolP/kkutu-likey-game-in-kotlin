package io.github.ranolp.waffle

import com.google.gson.JsonObject
import io.github.ranolp.waffle.settings.Setting
import org.junit.BeforeClass

class HttpTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun `mock setting`() {
            Setting._INSTANCE = Setting(JsonObject())
        }
    }
    // TODO: fix resource error
    /*
    @Test
    fun `test 404 page returns`() {
        withTestApplication(Application::module) {
            with(handleRequest(HttpMethod.Get, "/it/must/be/404")) {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }*/
}