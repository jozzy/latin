import 'package:flutter/material.dart';
import 'package:latin_ui/src/graph/models/nodes.dart';
import 'dart:math';

class GraphLinePainter extends CustomPainter {
  final Map<String, GraphNode> nodes;

  GraphLinePainter(this.nodes);

  @override
  void paint(Canvas canvas, Size size) {
    final plusPaint = Paint()
      ..color = Colors.grey.shade300
      ..strokeWidth = 1.0;

    const double spacing = 50;
    const double plusSize = 6;

    for (double x = 0; x < size.width; x += spacing) {
      for (double y = 0; y < size.height; y += spacing) {
        // Horizontaler Strich
        canvas.drawLine(
          Offset(x - plusSize / 2, y),
          Offset(x + plusSize / 2, y),
          plusPaint,
        );
        // Vertikaler Strich
        canvas.drawLine(
          Offset(x, y - plusSize / 2),
          Offset(x, y + plusSize / 2),
          plusPaint,
        );
      }
    }

    const double arrowLength = 16;
    const double arrowAngle = pi / 7;

    nodes.forEach((key, node) {
      for (var connectionKey in node.connections) {
        final connectedNode = nodes[connectionKey];

        if (connectedNode != null) {
          final Offset offsetX = Offset(
            connectedNode.width,
            connectedNode.height / 2,
          );
          final Offset offsetY = Offset(0, connectedNode.height / 2);
          final start = connectedNode.position + offsetX;
          final end = node.position + offsetY;
          final lineColor = node.active && connectedNode.active
              ? Colors.green
              : Colors.grey;
          final Paint linePaint = Paint()
            ..color = lineColor
            ..strokeWidth = 2.0;
          final Paint arrowPaint = Paint()
            ..color = lineColor
            ..strokeWidth = 2.0;

          canvas.drawLine(start, end, linePaint);

          final dx = end.dx - start.dx;
          final dy = end.dy - start.dy;
          final angle = atan2(dy, dx);

          final arrowP1 = Offset(
            end.dx - arrowLength * cos(angle - arrowAngle),
            end.dy - arrowLength * sin(angle - arrowAngle),
          );
          final arrowP2 = Offset(
            end.dx - arrowLength * cos(angle + arrowAngle),
            end.dy - arrowLength * sin(angle + arrowAngle),
          );

          canvas.drawLine(end, arrowP1, arrowPaint);
          canvas.drawLine(end, arrowP2, arrowPaint);
        }
      }
    });
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return false; // Assuming the graph is static
  }
}
