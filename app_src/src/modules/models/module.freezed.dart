// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'module.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$Module {

 String get name; String get version; String get instructions; String get description; Set<String> get triggers; Timer? get timer; String? get inputTemplate;
/// Create a copy of Module
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ModuleCopyWith<Module> get copyWith => _$ModuleCopyWithImpl<Module>(this as Module, _$identity);

  /// Serializes this Module to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is Module&&(identical(other.name, name) || other.name == name)&&(identical(other.version, version) || other.version == version)&&(identical(other.instructions, instructions) || other.instructions == instructions)&&(identical(other.description, description) || other.description == description)&&const DeepCollectionEquality().equals(other.triggers, triggers)&&(identical(other.timer, timer) || other.timer == timer)&&(identical(other.inputTemplate, inputTemplate) || other.inputTemplate == inputTemplate));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,version,instructions,description,const DeepCollectionEquality().hash(triggers),timer,inputTemplate);

@override
String toString() {
  return 'Module(name: $name, version: $version, instructions: $instructions, description: $description, triggers: $triggers, timer: $timer, inputTemplate: $inputTemplate)';
}


}

/// @nodoc
abstract mixin class $ModuleCopyWith<$Res>  {
  factory $ModuleCopyWith(Module value, $Res Function(Module) _then) = _$ModuleCopyWithImpl;
@useResult
$Res call({
 String name, String version, String instructions, String description, Set<String> triggers, Timer? timer, String? inputTemplate
});


$TimerCopyWith<$Res>? get timer;

}
/// @nodoc
class _$ModuleCopyWithImpl<$Res>
    implements $ModuleCopyWith<$Res> {
  _$ModuleCopyWithImpl(this._self, this._then);

  final Module _self;
  final $Res Function(Module) _then;

/// Create a copy of Module
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? version = null,Object? instructions = null,Object? description = null,Object? triggers = null,Object? timer = freezed,Object? inputTemplate = freezed,}) {
  return _then(_self.copyWith(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,version: null == version ? _self.version : version // ignore: cast_nullable_to_non_nullable
as String,instructions: null == instructions ? _self.instructions : instructions // ignore: cast_nullable_to_non_nullable
as String,description: null == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String,triggers: null == triggers ? _self.triggers : triggers // ignore: cast_nullable_to_non_nullable
as Set<String>,timer: freezed == timer ? _self.timer : timer // ignore: cast_nullable_to_non_nullable
as Timer?,inputTemplate: freezed == inputTemplate ? _self.inputTemplate : inputTemplate // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}
/// Create a copy of Module
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TimerCopyWith<$Res>? get timer {
    if (_self.timer == null) {
    return null;
  }

  return $TimerCopyWith<$Res>(_self.timer!, (value) {
    return _then(_self.copyWith(timer: value));
  });
}
}


/// Adds pattern-matching-related methods to [Module].
extension ModulePatterns on Module {
/// A variant of `map` that fallback to returning `orElse`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _Module value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _Module() when $default != null:
return $default(_that);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// Callbacks receives the raw object, upcasted.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case final Subclass2 value:
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _Module value)  $default,){
final _that = this;
switch (_that) {
case _Module():
return $default(_that);}
}
/// A variant of `map` that fallback to returning `null`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _Module value)?  $default,){
final _that = this;
switch (_that) {
case _Module() when $default != null:
return $default(_that);case _:
  return null;

}
}
/// A variant of `when` that fallback to an `orElse` callback.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  String version,  String instructions,  String description,  Set<String> triggers,  Timer? timer,  String? inputTemplate)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _Module() when $default != null:
return $default(_that.name,_that.version,_that.instructions,_that.description,_that.triggers,_that.timer,_that.inputTemplate);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// As opposed to `map`, this offers destructuring.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case Subclass2(:final field2):
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  String version,  String instructions,  String description,  Set<String> triggers,  Timer? timer,  String? inputTemplate)  $default,) {final _that = this;
switch (_that) {
case _Module():
return $default(_that.name,_that.version,_that.instructions,_that.description,_that.triggers,_that.timer,_that.inputTemplate);}
}
/// A variant of `when` that fallback to returning `null`
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  String version,  String instructions,  String description,  Set<String> triggers,  Timer? timer,  String? inputTemplate)?  $default,) {final _that = this;
switch (_that) {
case _Module() when $default != null:
return $default(_that.name,_that.version,_that.instructions,_that.description,_that.triggers,_that.timer,_that.inputTemplate);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _Module implements Module {
   _Module({required this.name, required this.version, required this.instructions, required this.description, required final  Set<String> triggers, this.timer, this.inputTemplate}): _triggers = triggers;
  factory _Module.fromJson(Map<String, dynamic> json) => _$ModuleFromJson(json);

@override final  String name;
@override final  String version;
@override final  String instructions;
@override final  String description;
 final  Set<String> _triggers;
@override Set<String> get triggers {
  if (_triggers is EqualUnmodifiableSetView) return _triggers;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableSetView(_triggers);
}

@override final  Timer? timer;
@override final  String? inputTemplate;

/// Create a copy of Module
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ModuleCopyWith<_Module> get copyWith => __$ModuleCopyWithImpl<_Module>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ModuleToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _Module&&(identical(other.name, name) || other.name == name)&&(identical(other.version, version) || other.version == version)&&(identical(other.instructions, instructions) || other.instructions == instructions)&&(identical(other.description, description) || other.description == description)&&const DeepCollectionEquality().equals(other._triggers, _triggers)&&(identical(other.timer, timer) || other.timer == timer)&&(identical(other.inputTemplate, inputTemplate) || other.inputTemplate == inputTemplate));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,version,instructions,description,const DeepCollectionEquality().hash(_triggers),timer,inputTemplate);

@override
String toString() {
  return 'Module(name: $name, version: $version, instructions: $instructions, description: $description, triggers: $triggers, timer: $timer, inputTemplate: $inputTemplate)';
}


}

/// @nodoc
abstract mixin class _$ModuleCopyWith<$Res> implements $ModuleCopyWith<$Res> {
  factory _$ModuleCopyWith(_Module value, $Res Function(_Module) _then) = __$ModuleCopyWithImpl;
@override @useResult
$Res call({
 String name, String version, String instructions, String description, Set<String> triggers, Timer? timer, String? inputTemplate
});


@override $TimerCopyWith<$Res>? get timer;

}
/// @nodoc
class __$ModuleCopyWithImpl<$Res>
    implements _$ModuleCopyWith<$Res> {
  __$ModuleCopyWithImpl(this._self, this._then);

  final _Module _self;
  final $Res Function(_Module) _then;

/// Create a copy of Module
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? version = null,Object? instructions = null,Object? description = null,Object? triggers = null,Object? timer = freezed,Object? inputTemplate = freezed,}) {
  return _then(_Module(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,version: null == version ? _self.version : version // ignore: cast_nullable_to_non_nullable
as String,instructions: null == instructions ? _self.instructions : instructions // ignore: cast_nullable_to_non_nullable
as String,description: null == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String,triggers: null == triggers ? _self._triggers : triggers // ignore: cast_nullable_to_non_nullable
as Set<String>,timer: freezed == timer ? _self.timer : timer // ignore: cast_nullable_to_non_nullable
as Timer?,inputTemplate: freezed == inputTemplate ? _self.inputTemplate : inputTemplate // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

/// Create a copy of Module
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TimerCopyWith<$Res>? get timer {
    if (_self.timer == null) {
    return null;
  }

  return $TimerCopyWith<$Res>(_self.timer!, (value) {
    return _then(_self.copyWith(timer: value));
  });
}
}


/// @nodoc
mixin _$Timer {

 String get interval;
/// Create a copy of Timer
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TimerCopyWith<Timer> get copyWith => _$TimerCopyWithImpl<Timer>(this as Timer, _$identity);

  /// Serializes this Timer to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is Timer&&(identical(other.interval, interval) || other.interval == interval));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,interval);

@override
String toString() {
  return 'Timer(interval: $interval)';
}


}

/// @nodoc
abstract mixin class $TimerCopyWith<$Res>  {
  factory $TimerCopyWith(Timer value, $Res Function(Timer) _then) = _$TimerCopyWithImpl;
@useResult
$Res call({
 String interval
});




}
/// @nodoc
class _$TimerCopyWithImpl<$Res>
    implements $TimerCopyWith<$Res> {
  _$TimerCopyWithImpl(this._self, this._then);

  final Timer _self;
  final $Res Function(Timer) _then;

/// Create a copy of Timer
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? interval = null,}) {
  return _then(_self.copyWith(
interval: null == interval ? _self.interval : interval // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [Timer].
extension TimerPatterns on Timer {
/// A variant of `map` that fallback to returning `orElse`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _Timer value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _Timer() when $default != null:
return $default(_that);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// Callbacks receives the raw object, upcasted.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case final Subclass2 value:
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _Timer value)  $default,){
final _that = this;
switch (_that) {
case _Timer():
return $default(_that);}
}
/// A variant of `map` that fallback to returning `null`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _Timer value)?  $default,){
final _that = this;
switch (_that) {
case _Timer() when $default != null:
return $default(_that);case _:
  return null;

}
}
/// A variant of `when` that fallback to an `orElse` callback.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String interval)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _Timer() when $default != null:
return $default(_that.interval);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// As opposed to `map`, this offers destructuring.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case Subclass2(:final field2):
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String interval)  $default,) {final _that = this;
switch (_that) {
case _Timer():
return $default(_that.interval);}
}
/// A variant of `when` that fallback to returning `null`
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String interval)?  $default,) {final _that = this;
switch (_that) {
case _Timer() when $default != null:
return $default(_that.interval);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _Timer implements Timer {
   _Timer({required this.interval});
  factory _Timer.fromJson(Map<String, dynamic> json) => _$TimerFromJson(json);

@override final  String interval;

/// Create a copy of Timer
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TimerCopyWith<_Timer> get copyWith => __$TimerCopyWithImpl<_Timer>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TimerToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _Timer&&(identical(other.interval, interval) || other.interval == interval));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,interval);

@override
String toString() {
  return 'Timer(interval: $interval)';
}


}

/// @nodoc
abstract mixin class _$TimerCopyWith<$Res> implements $TimerCopyWith<$Res> {
  factory _$TimerCopyWith(_Timer value, $Res Function(_Timer) _then) = __$TimerCopyWithImpl;
@override @useResult
$Res call({
 String interval
});




}
/// @nodoc
class __$TimerCopyWithImpl<$Res>
    implements _$TimerCopyWith<$Res> {
  __$TimerCopyWithImpl(this._self, this._then);

  final _Timer _self;
  final $Res Function(_Timer) _then;

/// Create a copy of Timer
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? interval = null,}) {
  return _then(_Timer(
interval: null == interval ? _self.interval : interval // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
