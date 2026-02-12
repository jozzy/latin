class Flow {
  final String id;
  final String name;
  final String? description;
  final List<String> steps;

  Flow({
    required this.id,
    required this.name,
    required this.description,
    required this.steps,
  });

  factory Flow.fromJson(Map<String, dynamic> json) {
    return Flow(
      id: json['id'] as String,
      name: json['name'] as String,
      description: json['description'] as String?,
      steps:
          (json['steps'] as List<dynamic>?)
              ?.map((e) => e.toString())
              .toList() ??
          [],
    );
  }
}
