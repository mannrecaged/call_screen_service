package com.kadvis.call_screen_service

data class CallScreenResponse(
    val disallow: Boolean = false,
    val silence: Boolean = false,
    val reject: Boolean = false,
    val skipNotification: Boolean = false,
    val skipCallLog: Boolean = false,
)