package studio.fabrique.backend.testcase.api

import com.sun.istack.NotNull
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import studio.fabrique.backend.testcase.configuration.jackson.Jackson
import studio.fabrique.backend.testcase.configuration.jackson.toJson
import studio.fabrique.backend.testcase.configuration.jackson.toObject
import studio.fabrique.backend.testcase.ignore

abstract class CrudApi<T>(
    protected val mvc: MockMvc,
    var jwt: String? = null,
) {

    abstract val url: String

    open fun get(pageNumber: Int = 0, pageSize: Int = 10, jwt: String? = this.jwt): ResultActionsDsl {
        val url =
            if (pageNumber == 0 && pageSize == 10) url
            else "$url?pageNumber=$pageNumber&pageSize=$pageSize"

        return mvc.get(url) {
            accept = MediaType.APPLICATION_JSON
            headers {
                jwt?.let(::setBearerAuth)
            }
        }.andDo { print() }
    }

    open fun get(entityId: Int): ResultActionsDsl =
        mvc.get("$url/$entityId") {
            accept = MediaType.APPLICATION_JSON
            headers {
                jwt?.let(::setBearerAuth)
            }
        }.andDo { print() }

    open fun post(entity: T, jwt: String? = this.jwt): ResultActionsDsl =
        post(entity.toJson(), jwt)

    open fun post(entityJson: String, jwt: String? = this.jwt): ResultActionsDsl =
        mvc.post(url) {
            contentType = MediaType.APPLICATION_JSON
            content = entityJson
            accept = MediaType.APPLICATION_JSON
            headers {
                jwt?.let(::setBearerAuth)
            }
        }.andDo { print() }

    open fun put(entityId: Int, entity: T, jwt: String? = this.jwt): ResultActionsDsl =
        put(entityId, entity.toJson(), jwt)

    private fun put(entityId: Int, entityJson: String, jwt: String? = this.jwt): ResultActionsDsl =
        mvc.put("$url/$entityId") {
            contentType = MediaType.APPLICATION_JSON
            content = entityJson
            accept = MediaType.APPLICATION_JSON
            headers {
                jwt?.let(::setBearerAuth)
            }
        }.andDo { print() }

}

var MockHttpServletRequestDsl.authorization: String?
    get() {
        var value: String? = null
        headers {
            ignore<NoSuchElementException> {
                value = getValue(HttpHeaders.AUTHORIZATION).first()
            }
        }
        return value
    }
    set(value) = value?.let { this.headers { setBearerAuth(value) } }!!

inline fun <reified T> ResultActionsDsl.andReturnOfType() =
    this.andReturn().response.contentAsString.toObject<T>()
