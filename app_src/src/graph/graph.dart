import 'package:flutter/material.dart';
import 'package:latin_ui/src/graph/graph_line_painter.dart';
import 'package:latin_ui/src/graph/models/nodes.dart';
import 'package:latin_ui/src/graph/node_card.dart';
import 'package:latin_ui/src/playground/input_panel.dart';
import 'package:smiles/smiles.dart';

class Graph extends StatelessWidget {
  final Map<String, GraphNode> nodes;

  const Graph({super.key, required this.nodes});

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        // The CustomPaint widget to draw the lines
        Positioned.fill(child: CustomPaint(painter: GraphLinePainter(nodes))),

        // The card widgets, positioned on top
        ...nodes.entries.map((entry) {
          final node = entry.value;
          return Positioned(
            left: node.position.dx,
            top: node.position.dy,
            child: NodeCard(node),
          );
        }).toList(),
      ],
    );
  }
}
