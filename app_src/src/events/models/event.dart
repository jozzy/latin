import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:latin_ui/src/modules/models/module.dart';
import 'package:smiles/smiles.dart';

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

extension EventExtension on Event {
  String? get output => data['output']?.toString();
}

extension EventsExtension on List<Event> {
  Event? findCompletedFor(Module module) => findFirst(
    (e) => (e.data['moduleId'] != null && module.name == e.data['moduleId']),
  );

  Event? findHandoverFor(Module module) => findFirst(
    (e) =>
        (e.data['fromModule'] != null && module.name == e.data['fromModule']),
  );
}
