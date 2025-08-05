import 'package:freezed_annotation/freezed_annotation.dart';

part 'module.freezed.dart';

part 'module.g.dart';

@freezed
sealed class Module with _$Module {
  factory Module({
    required String name,
    required String version,
    required String instructions,
    required String description,
    required Set<String> triggers,
    Timer? timer,
    String? inputTemplate,
  }) = _Module;

  factory Module.fromJson(Map<String, dynamic> json) => _$ModuleFromJson(json);
}

@freezed
sealed class Timer with _$Timer {
  factory Timer({
    required String interval,
  }) = _Timer;

  factory Timer.fromJson(Map<String, dynamic> json) => _$TimerFromJson(json);
}
