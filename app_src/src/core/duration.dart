String format(String duration) {
  final unit = switch (duration) {
    var s when s.contains('H') => 'hours',
    var s when s.contains('M') => 'minutes',
    var s when s.contains('S') => 'seconds',
    _ => 'seconds',
  };
  final value = extractNumber(duration);
  final formattedValue = value != null
      ? double.parse(value).toStringAsPrecision(2)
      : '';
  return '$formattedValue $unit';
}

final numberRegex = RegExp(r'(\d+\.?\d*)');

String? extractNumber(String duration) {
  final match = numberRegex.firstMatch(duration);
  return match?.group(0);
}
