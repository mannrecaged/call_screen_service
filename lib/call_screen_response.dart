class CallScreenResponse {
  final bool disallow;
  final bool silence;
  final bool reject;
  final bool skipNotification;

  CallScreenResponse(
      {this.disallow = false,
      this.silence = false,
      this.reject = false,
      this.skipNotification = false});



  Map<String, dynamic> toJson() => {
        'disallow': disallow,
        'silence': silence,
        'reject': reject,
        'skipNotification': skipNotification,
      };

  CallScreenResponse.fromJson(Map<String, dynamic> json)
      : disallow = json['disallow'],
        silence = json['silence'],
        reject = json['reject'],
        skipNotification = json['skipNotification'];
}
