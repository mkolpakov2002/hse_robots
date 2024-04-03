package ru.hse.control_system_v2.model.entities.universal

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorClient(private val baseUrl: String, private val token: String) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(
            Json {
                ignoreUnknownKeys = true
            })
        }
        HttpResponseValidator {
            validateResponse { response ->
                val error: Error = response.body()
                Log.e("KtorClient error", error.toString())
            }
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        defaultRequest {
            url(baseUrl)
            header("Authorization", "OAuth $token")
        }
    }
}