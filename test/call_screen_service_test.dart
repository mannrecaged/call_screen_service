import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:call_screen_service/call_screen_service.dart';

void main() {
  const MethodChannel channel = MethodChannel('call_screen_service');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await CallScreenService.platformVersion, '42');
  });
}
