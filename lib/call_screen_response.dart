class CallScreenResponse {
  final bool silence;
  final bool reject;
  final bool skipNotification;

  CallScreenResponse(
      {this.silence = false,
      this.reject = false,
      this.skipNotification = false});

  Map<String, dynamic> toJson() => {
        'silence': silence,
        'reject': reject,
        'skipNotification': skipNotification,
      };

  CallScreenResponse.fromJson(Map<String, dynamic> json)
      : silence = json['silence'],
        reject = json['reject'],
        skipNotification = json['skipNotification'];
}
