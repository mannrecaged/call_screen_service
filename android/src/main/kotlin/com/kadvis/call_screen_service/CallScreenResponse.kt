package com.kadvis.call_screen_service

import androidx.annotation.Keep

@Keep
data class CallScreenResponse(
    val silence: Boolean = false,
    val reject: Boolean = false,
    val skipNotification: Boolean = false,

)