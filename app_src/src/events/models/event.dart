import 'package:freezed_annotation/freezed_annotation.dart';

part 'event.freezed.dart';

part 'event.g.dart';

@freezed
sealed class Event with _$Event {
  factory Event({
    required String id,
    required String event,
    required Map<String, dynamic> data,
  }) = _Event;

  factory Event.fromJson(Map<String, dynamic> json) => _$EventFromJson(json);
}
