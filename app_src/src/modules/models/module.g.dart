// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'module.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_Module _$ModuleFromJson(Map<String, dynamic> json) => _Module(
  name: json['name'] as String,
  version: json['version'] as String,
  instructions: json['instructions'] as String,
  description: json['description'] as String,
  triggers: (json['triggers'] as List<dynamic>).map((e) => e as String).toSet(),
  timer: json['timer'] == null
      ? null
      : Timer.fromJson(json['timer'] as Map<String, dynamic>),
  inputTemplate: json['inputTemplate'] as String?,
);

Map<String, dynamic> _$ModuleToJson(_Module instance) => <String, dynamic>{
  'name': instance.name,
  'version': instance.version,
  'instructions': instance.instructions,
  'description': instance.description,
  'triggers': instance.triggers.toList(),
  'timer': instance.timer,
  'inputTemplate': instance.inputTemplate,
};

_Timer _$TimerFromJson(Map<String, dynamic> json) =>
    _Timer(interval: json['interval'] as String);

Map<String, dynamic> _$TimerToJson(_Timer instance) => <String, dynamic>{
  'interval': instance.interval,
};
