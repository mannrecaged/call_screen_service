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
  const MethodChannel _backgroundChannel = MethodChannel(backgroundChannelName);

  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
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

    //
    // final List<dynamic> args = call.arguments;
    final Function? callback = PluginUtilities.getCallbackFromHandle(
        CallbackHandle.fromRawHandle(userCallbackRawHandle));
    if (callback == null) {
      debugPrint("No callback found");
      return;
    }

    final CallScreenResponse response = await callback(callScreenInfo);
    final jsonStr = json.encode(response.toJson());
    return Future.value(jsonStr);

    // assert(callback != null);
    // final List<String> triggeringGeofences = args[1].cast<String>();
    // final List<double> locationList = <double>[];
    // // 0.0 becomes 0 somewhere during the method call, resulting in wrong
    // // runtime type (int instead of double). This is a simple way to get
    // // around casting in another complicated manner.
    // args[2]
    //     .forEach((dynamic e) => locationList.add(double.parse(e.toString())));
    // final Location triggeringLocation = locationFromList(locationList);
    // final GeofenceEvent event = intToGeofenceEvent(args[3]);
    // callback!(triggeringGeofences, triggeringLocation, event);1
  });
  debugPrint("before background flutter/dart initialized");
  await _backgroundChannel.invokeMethod(initializedMethodName);
  debugPrint("after background flutter/dart initialized");
}
