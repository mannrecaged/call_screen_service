// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:convert';
import 'dart:ui';

import 'package:call_screen_service/call_screen_info.dart';
import 'package:call_screen_service/call_screen_response.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const backgroundChannelName =
    "CallScreenServicePlugin.callScreenChannelBackground";

const initializedMethodName = "CallScreenServicePlugin.initialized";


@pragma('vm:entry-point')
Future<void> callbackDispatcher() async {
  debugPrint("callback dispatches inside");
  WidgetsFlutterBinding.ensureInitialized();
  DartPluginRegistrant.ensureInitialized();

  const MethodChannel _backgroundChannel = MethodChannel(backgroundChannelName);
  _backgroundChannel.setMethodCallHandler(platformMethodCall);
  debugPrint("before background flutter/dart initialized");
  await _backgroundChannel.invokeMethod(initializedMethodName);
  debugPrint("after background flutter/dart initialized");
}

Future<dynamic> platformMethodCall(MethodCall call) async {
  debugPrint("call from native. received at flutter");

  debugPrint("Call details: ${call.arguments}");

  final arguments = call.arguments as List<Object?>?;

  if (arguments == null) {
    debugPrint("Arguments are null. value: $arguments");
    return;
  }

  final userCallbackRawHandle = arguments[0] as int;
  final callScreenInfoStr = arguments[1] as String;

  if (callScreenInfoStr.isEmpty) {
    return;
  }

  final callScreenInfo =
  CallScreenInfo.fromJson(json.decode(callScreenInfoStr));

  final Function? callback = PluginUtilities.getCallbackFromHandle(
      CallbackHandle.fromRawHandle(userCallbackRawHandle));
  if (callback == null) {
    debugPrint("No callback found");
    return;
  }

  final CallScreenResponse response = await callback(callScreenInfo);
  final jsonStr = json.encode(response.toJson());
  return Future.value(jsonStr);
}
