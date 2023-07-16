import 'dart:async';
import 'dart:ui';

import 'package:call_screen_service/call_screen_response.dart';
import 'package:call_screen_service/src/callback_dispatcher.dart';
import 'package:flutter/services.dart';

const foregroundChannelName = "CallScreenServicePlugin.call_screen_foreground_channel";
const initializeServiceMethod = "CallScreenServicePlugin.initializeService";



class CallScreenService {
  // static const String userCallbackHandleKey = "userCallbackHandleKey";

  static const MethodChannel _channel = MethodChannel(foregroundChannelName);

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  // static Future<void> _initializeService(
  //     Function(String phone) callback) async {
  //   final CallbackHandle? callback =
  //       PluginUtilities.getCallbackHandle(callbackDispatcher);
  //
  //   final CallbackHandle? userCallback =
  //       PluginUtilities.getCallbackHandle(callbackDispatcher);
  //
  //   await _channel.invokeMethod(
  //       'initializeService', <dynamic>[callback?.toRawHandle(), userCallback]);
  // }

  static Future<void> initialise(
      Future<CallScreenResponse> Function(String phone)? userCallback) async {
    if (userCallback == null) {
      throw Exception("callback should be provided");
    }
    // // await _initializeService(callback);
    // final handle = PluginUtilities.getCallbackHandle(userCallback);
    //
    // if(handle == null){
    //   throw Exception("Invalid handle or its not available");
    // }
    final CallbackHandle? callback =
        PluginUtilities.getCallbackHandle(callbackDispatcher);

    final CallbackHandle? userCallbackHandle =
        PluginUtilities.getCallbackHandle(userCallback);

    if (userCallbackHandle == null) {
      throw Exception("Invalid handle or its not available");
    }

    await _channel.invokeMethod(initializeServiceMethod,
        <dynamic>[callback?.toRawHandle(), userCallbackHandle.toRawHandle()]);
  }
}
