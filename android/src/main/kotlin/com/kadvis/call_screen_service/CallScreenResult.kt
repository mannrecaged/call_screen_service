package com.kadvis.call_screen_service

import android.os.Build
import android.telecom.Call.Details
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import io.flutter.plugin.common.MethodChannel

open class CallScreenResult(
    private val callScreenService: CallScreeningService,
    private val callDetails: Details
) : MethodChannel.Result {

    companion object {
        const val TAG = "DefaultMethodResult"
        val gson = Gson()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun success(result: Any?) {
        Log.d(TAG, "DefaultMethodResult.success. result: $result")

        if (result == null) {
            return
        }

        if (result !is String) {
            Log.d(TAG,"It not a string. result: $result")
            return
        }

        val callScreenResponse = gson.fromJson(result, CallScreenResponse::class.java)
        respond(callScreenResponse)
        Log.i(TAG,"call screen responded: $callScreenResponse")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun respond(callScreenResponse: CallScreenResponse) {
        val builder = CallScreeningService.CallResponse.Builder()
        builder.setSkipCallLog(false)
        builder.setDisallowCall(callScreenResponse.reject)
        builder.setSilenceCall(callScreenResponse.silence)
        builder.setRejectCall(callScreenResponse.reject)
        builder.setSkipNotification(callScreenResponse.skipNotification)
        callScreenService.respondToCall(callDetails, builder.build())
    }

    override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
        Log.i(
            TAG,
            "DefaultMethodResult.error. errorCode: $errorCode, errorMessage: $errorMessage, errorDetails: $errorDetails "
        )
    }

    override fun notImplemented() {
        Log.i(TAG, "DefaultMethodResult.notImplemented")
    }
}