enum CallDirection { incoming, outgoing, unknown }

class CallScreenInfo {
  final String phone;
  final CallDirection direction;

  CallScreenInfo(this.phone, this.direction);

  Map<String, dynamic> toJson() => {
        'phone': phone,
        'direction': _toDirectionInt(direction),
      };

  int _toDirectionInt(CallDirection direction) {
    switch (direction) {
      case CallDirection.incoming:
        return 0;
      case CallDirection.outgoing:
        return 1;
      default:
        return -1;
    }
  }

  static CallDirection _toDirectionEnum(int direction) {
    switch (direction) {
      case 0:
        return CallDirection.incoming;
      case 1:
        return CallDirection.outgoing;
      default:
        return CallDirection.unknown;
    }
  }

  CallScreenInfo.fromJson(Map<String, dynamic> json)
      : phone = json['phone'],
        direction = _toDirectionEnum(json['direction']);
}
