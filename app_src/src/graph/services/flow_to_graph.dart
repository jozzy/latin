import 'dart:convert';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:http/http.dart' as http;
import 'package:latin_ui/src/events/models/event.dart';
import 'package:latin_ui/src/graph/graph_line_painter.dart';
import 'package:latin_ui/src/graph/models/nodes.dart';
import 'package:latin_ui/src/modules/models/module.dart';
import 'package:latin_ui/src/modules/notifiers/current_flow.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:smiles/smiles.dart';

Map<String, GraphNode> buildGraph(
  List<Module> flow,
  List<Event>? events,
  List<Module>? allModules,
) {
  var currentXLevel = 10.0;
  const currentXSpacing = 150.0;
  const currentYLevel = 200.0;
  var nodes = <GraphNode>[];
  var lastHandoverNodes = <String>[];
  String? lastModuleNodeId;

  // Add a start node at the beginning of the graph
  nodes.add(StartGraphNode(position: Offset(currentXLevel, currentYLevel)));
  currentXLevel += nodes.last.width + currentXSpacing;

  // Iterate through the flow and create nodes for each module
  for (var i = 0; i < flow.length; i++) {
    final module = flow[i];
    final event = events?.findCompletedFor(module);
    final executed = event != null;
    final previousNode = nodes[i];

    lastModuleNodeId = '${module.name}-$i';
    nodes.add(
      ModuleGraphNode(
        id: lastModuleNodeId,
        module: module,
        active: executed,
        output: event?.output,
        position: Offset(currentXLevel, currentYLevel),
        connections: [previousNode.id],
      ),
    );
    currentXLevel += nodes.last.width + currentXSpacing;

    // Add handover nodes if the module has handovers
    var x = 1;
    module.handovers?.forEach((handover) {
      final handoverModule = allModules?.findFirst((m) => m.name == handover);
      if (handoverModule != null) {
        final event = events?.findCompletedFor(handoverModule);
        final nodeId = '${handoverModule.name}-$i-$x';
        nodes.add(
          ModuleGraphNode(
            id: nodeId,
            module: handoverModule,
            active: event != null,
            removable: false,
            icon: Icons.swap_horiz,
            output: event?.output,
            position: Offset(
              currentXLevel,
              currentYLevel + ((nodes.last.height + 10) * x),
            ),
            connections: [lastModuleNodeId!],
          ),
        );
        lastHandoverNodes.add(nodeId);
        x++;
      }
    });
    if (module.handovers != null && module.handovers!.isNotEmpty) {
      currentXLevel += nodes.last.width + currentXSpacing;
    } else {
      lastHandoverNodes = [];
    }

    //offset = 10.0 + (nodes.length * 450);
    // x = 1;
    //module.outputSymbols?.forEach((symbol) {
    // nodes.add(
    //   OutputGraphNode(
    //     id: '${module.name}_${symbol.hashCode}',
    //     response: symbol,
    //     position: Offset(offset, 100.0 * x),
    //     connections: [module.name],
    //     ),
    //   );
    // x++;
    //});
  }

  // Add end node at the end of the graph.
  nodes.add(
    EndGraphNode(
      position: Offset(currentXLevel, currentYLevel),
      connections: [?lastModuleNodeId],
    ),
  );
  return {for (var node in nodes) node.id: node};
}
