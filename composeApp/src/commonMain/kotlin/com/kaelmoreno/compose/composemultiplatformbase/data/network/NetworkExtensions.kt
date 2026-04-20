package com.kaelmoreno.compose.composemultiplatformbase.data.network

import com.kaelmoreno.compose.composemultiplatformbase.Platform
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

fun HttpRequestBuilder.addDeviceHeaders(platform: Platform) {
    headers {
        append("X-Device-FCM-Token", platform.fcmKey)
        append("X-Device-UDID", platform.deviceUDID)
        append("X-Device-OS", platform.deviceOS)
        append("X-Device-App-Version", platform.appVersion)
        append("X-Device-OS-Version", platform.deviceOSVersion)
        append("X-Device-Manufacturer", platform.deviceManufacturer)
        append("X-Device-Model", platform.deviceModel)
    }
}

inline fun <reified T> enqueue(
    platform: Platform,
    body: Any? = null,
    httpMethod: HttpMethods,
    httpEndpoint: String,
    vararg query: Pair<String, String> = emptyArray(),
    crossinline onSuccessResult: (headers: Headers, result: T) -> T = { _, result -> result }
): Flow<ResponseHandler<T>> {
    val client = NetworkClient.httpClient
    val json = Json { ignoreUnknownKeys = true }
    return flow {
        emit(ResponseHandler.Loading<T>())

        runCatching {
            when (httpMethod) {
                HttpMethods.GET -> {
                    client.get(httpEndpoint) {
                        addDeviceHeaders(platform)
                        query.forEach { parameter(it.first, it.second) }
                    }
                }
                HttpMethods.POST -> {
                    client.post(httpEndpoint) {
                        addDeviceHeaders(platform)
                        setBody(body)
                        query.forEach { parameter(it.first, it.second) }
                    }
                }
                HttpMethods.DELETE -> {
                    client.delete(httpEndpoint) {
                        addDeviceHeaders(platform)
                        setBody(body)
                    }
                }
                HttpMethods.PUT -> {
                    client.put(httpEndpoint) {
                        addDeviceHeaders(platform)
                        setBody(body)
                    }
                }
            }
        }.onSuccess { result ->
            if (result.status.isSuccess()) {
                val successResult = onSuccessResult(result.headers, result.body())
                emit(ResponseHandler.Success(data = successResult))
            } else {
                val errorBody: String = result.body()
                var apiError = ApiError(
                    error = Error(
                        httpCode = -1,
                        code = "UNKNOWN_ERROR",
                        message = "An error occurred. Please try again."
                    ),
                    httpUrl = httpEndpoint
                )
                if (errorBody.isNotEmpty()) {
                    try {
                        apiError = json.decodeFromString<ApiError>(errorBody)
                            .copy(httpUrl = httpEndpoint)
                    } catch (_: Exception) {
                    }
                }
                emit(ResponseHandler.Error<T>(apiError = apiError))
            }
        }.onFailure {
            emit(ResponseHandler.Failure<T>(exception = it))
        }
    }
}
