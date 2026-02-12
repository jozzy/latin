import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:latin_ui/src/core/collections.dart';
import 'package:latin_ui/src/core/dialog_header.dart';
import 'package:latin_ui/src/events/notifiers/fetch_events.dart';
import 'package:latin_ui/src/graph/graph.dart';
import 'package:latin_ui/src/graph/graph_line_painter.dart';
import 'package:latin_ui/src/graph/models/nodes.dart';
import 'package:latin_ui/src/graph/services/flow_to_graph.dart';
import 'package:latin_ui/src/modules/models/module.dart';
import 'package:latin_ui/src/modules/notifiers/current_flow.dart';
import 'package:latin_ui/src/modules/notifiers/modules.dart';
import 'package:latin_ui/src/modules/syntax_text_controller.dart';
import 'package:latin_ui/src/playground/input_panel.dart';
import 'package:smiles/smiles.dart';

class GraphScreen extends StatelessWidget {
  const GraphScreen({super.key, this.module});

  final Module? module;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: [
          CircleAvatar(
            child: Icon(
              Icons.account_tree_rounded,
              color: Colors.white,
              size: 18,
            ),
          ),
          HGap(),
          ListTile(
            dense: true,
            title: 'Latin Playground'.h2,
            subtitle: 'A playground for Latin modules.'.txt,
          ).padding().expand(),
        ].row(),
      ),
      body: Stack(
        children: [
          // The graph viewer
          InteractiveViewer(
            child: SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: SizedBox(
                width: 4000,
                height: 2000,
                child: Consumer(
                  builder: (context, ref, _) {
                    final modules = ref.watch(modulesProvider).valueOrNull;
                    final flow = ref.watch(currentFlowProvider);
                    final events = ref.watch(eventsProvider).valueOrNull;
                    final Map<String, GraphNode> nodes = buildGraph(
                      flow,
                      events,
                      modules,
                    );
                    return Graph(nodes: nodes);
                  },
                ),
              ),
            ),
          ),

          // The input panel
          Positioned(
            left: 20,
            bottom: 20,
            child: InputPanel().size(height: 300, width: 500),
          ),
        ],
      ),
    );
  }
}
