import 'dart:async';
import 'dart:ui';

import 'package:call_screen_service/call_screen_response.dart';
import 'package:call_screen_service/src/callback_dispatcher.dart';
import 'package:flutter/services.dart';

import 'call_screen_info.dart';

const foregroundChannelName =
    "CallScreenServicePlugin.call_screen_foreground_channel";
const initializeServiceMethod = "CallScreenServicePlugin.initializeService";

class CallScreenService {
  static const MethodChannel _channel = MethodChannel(foregroundChannelName);

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> initialise(
      Future<CallScreenResponse> Function(CallScreenInfo callScreenInfo)?
          userCallback) async {
    if (userCallback == null) {
      throw Exception("callback should be provided");
    }
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
