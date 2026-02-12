import 'package:flutter/material.dart';
import 'package:latin_ui/src/modules/models/module.dart';

abstract class GraphNode {
  GraphNode({
    required this.id,
    required this.position,
    required this.connections,
    required this.width,
    required this.height,
    this.active = false,
  });

  final String id;
  final Offset position;
  final List<String> connections;
  final double width;
  final double height;
  final bool active;
}

class ModuleGraphNode extends GraphNode {
  ModuleGraphNode({
    required super.id,
    required this.module,
    this.output,
    this.removable = true,
    this.icon,
    required super.position,
    required super.connections,
    required super.active,
  }) : super(width: 300, height: 80);

  final Module module;
  final String? output;
  final bool removable;
  final IconData? icon;
}

class OutputGraphNode extends GraphNode {
  OutputGraphNode({
    required this.response,
    required super.id,
    required super.position,
    required super.connections,
  }) : super(width: 300, height: 80);

  final String response;
}

class EndGraphNode extends GraphNode {
  EndGraphNode({required super.position, required super.connections})
    : super(id: 'end', width: 80, height: 80, active: true);
}

class StartGraphNode extends GraphNode {
  StartGraphNode({required super.position, super.connections = const []})
    : super(id: 'start', width: 100, height: 80, active: true);
}
