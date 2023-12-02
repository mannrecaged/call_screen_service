# call_screen_service

This Flutter plugin provides call screening functionality using Android's CallScreeningService. It allows you to intercept incoming calls and handle them accordingly, such as blocking or answering them. 
## Getting Started

Define your callback function

    @pragma('vm:entry-point')
    Future<CallScreenResponse> receivedCall(CallScreenInfo info) {
        debugPrint("Ya!, received call from phone number: ${info.phone}");
        return Future.value(CallScreenResponse(reject: true));
    }

Once function is defined. Initialize the plugin

    await CallScreenService.initialise(receivedCall);


