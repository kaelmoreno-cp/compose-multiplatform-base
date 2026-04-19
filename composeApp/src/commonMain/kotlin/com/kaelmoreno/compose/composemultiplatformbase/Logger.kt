package com.kaelmoreno.compose.composemultiplatformbase

import co.touchlab.kermit.Logger as KermitLogger

object Logger {

    fun initialize() {
        // Kermit initializes automatically with default config
        KermitLogger.setTag("App")
    }

    fun d(message: String, tag: String? = null) {
        KermitLogger.d(tag ?: "App") { message }
    }

    fun i(message: String, tag: String? = null) {
        KermitLogger.i(tag ?: "App") { message }
    }

    fun w(message: String, tag: String? = null) {
        KermitLogger.w(tag ?: "App") { message }
    }

    fun e(message: String, throwable: Throwable? = null, tag: String? = null) {
        if (throwable != null) {
            KermitLogger.e(tag ?: "App", throwable) { message }
        } else {
            KermitLogger.e(tag ?: "App") { message }
        }
    }

    fun v(message: String, tag: String? = null) {
        KermitLogger.v(tag ?: "App") { message }
    }
}
