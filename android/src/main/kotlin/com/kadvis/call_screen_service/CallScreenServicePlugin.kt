package com.kadvis.call_screen_service

import android.content.Context
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.view.FlutterCallbackInformation
import io.flutter.view.FlutterMain
import java.util.Objects

/** CallScreenServicePlugin */
@RequiresApi(Build.VERSION_CODES.N)
class CallScreenServicePlugin : FlutterPlugin, MethodCallHandler {

    companion object {
        const val TAG = "CallScreenServicePlugin"
        const val SHARED_PREFERENCES_KEY = "CallScreenServicePlugin.call_screen_service_plugin_cache"
        const val CALLBACK_DISPATCHER_HANDLE_KEY = "CallScreenServicePlugin.callback_dispatch_handler"
        const val USER_CALLBACK_HANDLE_KEY = "CallScreenServicePlugin.user_callback_handler"
        const val CALL_SCREEN_METHOD_CHANNEL = "CallScreenServicePlugin.call_screen_foreground_channel"
        const val METHOD_INITIALIZE_SERVICE = "CallScreenServicePlugin.initializeService"
    }

    private lateinit var channel: MethodChannel
    private lateinit var context: Context


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, CALL_SCREEN_METHOD_CHANNEL)
        channel.setMethodCallHandler(this)
        Log.i(TAG, "Attached to engine")
    }


    private fun initializeService(args: ArrayList<*>?) {

        val callbackDispatcher = args!![0] as Long
        val userCallBack = args[1] as Long
        Log.d(TAG, "Initializing CallScreenServicePlugin. callbackDispatcher: $callbackDispatcher, userCallBack: $userCallBack")
        context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
            .edit()
            .putLong(CALLBACK_DISPATCHER_HANDLE_KEY, callbackDispatcher)
            .putLong(USER_CALLBACK_HANDLE_KEY, userCallBack)
            .apply()
        Log.d(TAG, "----Initialized CallScreenServicePlugin----")
    }


    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {

        when (call.method) {
            METHOD_INITIALIZE_SERVICE -> {
                initializeService(call.arguments as ArrayList<*>?)
                result.success(true)
            }

            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }

            else -> result.notImplemented()
        }

    }


    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

}
