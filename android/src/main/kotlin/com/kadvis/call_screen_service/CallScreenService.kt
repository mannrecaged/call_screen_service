package com.kadvis.call_screen_service

import android.content.Context
import android.os.Build
import android.telecom.Call
import android.telecom.Call.Details
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.view.FlutterCallbackInformation

@RequiresApi(Build.VERSION_CODES.N)
class CallScreenService : CallScreeningService(), MethodChannel.MethodCallHandler {

    companion object {
        const val TAG = "CallScreenService"
        private val lock = Object()
        const val CALL_SCREEN_CHANNEL_BACKGROUND =
            "CallScreenServicePlugin.callScreenChannelBackground"
        const val METHOD_INITIALIZED = "CallScreenServicePlugin.initialized"
        const val METHOD_ON_SCREEN_CALL = "CallScreenServicePlugin.onScreenCall"
        val gson = Gson()
    }

    private lateinit var backgroundChannel: MethodChannel
    private var flutterEngine: FlutterEngine? = null
    private lateinit var result: CallScreenResult

    override fun onCreate() {
        super.onCreate()
        startCallScreenService()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onScreenCall(callDetails: Call.Details) {
        screenCall(callDetails)
    }


    private fun startCallScreenService() {
        synchronized(lock) {
            if (flutterEngine == null) {
                flutterEngine = FlutterEngine(this)
            }
        }

        val callbackHandle = getSharedPreferences(
            CallScreenServicePlugin.SHARED_PREFERENCES_KEY,
            Context.MODE_PRIVATE
        )
            .getLong(CallScreenServicePlugin.CALLBACK_DISPATCHER_HANDLE_KEY, 0)
        if (callbackHandle == 0L) {
            Log.e(TAG, "Fatal: no callback registered")
            return
        }

        val callbackInfo =
            FlutterCallbackInformation.lookupCallbackInformation(callbackHandle)
        if (callbackInfo == null) {
            Log.e(TAG, "Fatal: failed to find callback")
            return
        }
        Log.d(TAG, "Starting CallScreenService...")

        val dartCallback = DartExecutor.DartCallback(
            assets,
            FlutterInjector.instance().flutterLoader().findAppBundlePath(),
            callbackInfo
        )
        val dartExecutor = flutterEngine?.dartExecutor ?: return
        dartExecutor.executeDartCallback(dartCallback)
        backgroundChannel = MethodChannel(
            dartExecutor.binaryMessenger,
            CALL_SCREEN_CHANNEL_BACKGROUND
        )
        backgroundChannel.setMethodCallHandler(this)
        Log.d(TAG, "Started CallScreenService...")
    }

    private fun getUserCallback(): Long {

        val prefs = getSharedPreferences(
            CallScreenServicePlugin.SHARED_PREFERENCES_KEY,
            Context.MODE_PRIVATE
        )
        return prefs.getLong(CallScreenServicePlugin.USER_CALLBACK_HANDLE_KEY, 0)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            METHOD_INITIALIZED -> {
                Log.d(TAG, "Dart initialised successfully")
                result.success(true)
            }
            else -> result.notImplemented()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun screenCall(callDetails: Details) {
        result = CallScreenResult(this, callDetails)
        val phone = callDetails.handle.schemeSpecificPart
        Log.i(TAG, "Screening call from: $phone")
        val callScreenInfo = CallScreenInfo(phone, callDetails.callDirection)
        val callScreenStr = gson.toJson(callScreenInfo)
        val args = arrayListOf<Any>(getUserCallback(), callScreenStr)
        backgroundChannel.invokeMethod(
            METHOD_ON_SCREEN_CALL,
            args,
            result
        )
        Log.d(TAG, "Invoked platform method: $METHOD_ON_SCREEN_CALL")
    }
}