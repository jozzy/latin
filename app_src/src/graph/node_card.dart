import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:latin_ui/src/graph/models/nodes.dart';
import 'package:latin_ui/src/modules/module_dialog.dart';
import 'package:latin_ui/src/modules/notifiers/current_flow.dart';
import 'package:latin_ui/src/modules/notifiers/modules.dart';
import 'package:smiles/smiles.dart';

class NodeCard extends ConsumerWidget {
  const NodeCard(this.node, {super.key});

  final GraphNode node;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    switch (node) {
      case OutputGraphNode node:
        return _buildOutputGraphNode(node, context, ref);
      case ModuleGraphNode node:
        return _buildModuleGraphNode(node, context, ref);
      case StartGraphNode node:
        return _buildStartGraphNode(node, context, ref);
      case EndGraphNode node:
        return _buildEndGraphNode(node, context, ref);
      default:
        return SizedBox.shrink();
    }
  }

  Widget _buildStartGraphNode(
    StartGraphNode node,
    BuildContext context,
    WidgetRef ref,
  ) {
    return Card(
      color: context.colorScheme.primary,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      elevation: 4,
      child: 'START'
          .style(color: context.colorScheme.onSecondary)
          .center()
          .padByUnits(2, 2, 2, 2),
    ).size(width: node.width, height: node.height);
  }

  Widget _buildEndGraphNode(
    EndGraphNode node,
    BuildContext context,
    WidgetRef ref,
  ) {
    return Card(
      color: context.colorScheme.secondary,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      elevation: 4,
      child: 'END'
          .style(color: context.colorScheme.onSecondary)
          .center()
          .padByUnits(2, 2, 2, 2),
    ).size(width: node.width, height: node.height);
  }

  Widget _buildOutputGraphNode(
    OutputGraphNode node,
    BuildContext context,
    WidgetRef ref,
  ) {
    return Card(
      elevation: 4,
      child: ListTile(
        leading: Icon(
          Icons.output_outlined,
          color: context.colorScheme.primary,
        ),
        title: node.response.txt,
      ),
    ).size(width: node.width, height: node.height);
  }

  Widget _buildModuleGraphNode(
    ModuleGraphNode node,
    BuildContext context,
    WidgetRef ref,
  ) {
    final currentNode =
        ref.watch(
          modulesProvider.select(
            (m) => m.valueOrNull?.findFirst((m) => m.name == node.id),
          ),
        ) ??
        node.module;
    return Column(
      children: [
        Card(
          elevation: 4,
          child: ListTile(
            leading: Icon(
              node.icon ?? Icons.settings,
              color: context.colorScheme.primary,
            ),
            title: currentNode.name.txt,
            subtitle:
                (currentNode.description.isNotEmpty
                        ? currentNode.description
                        : 'No description.')
                    .txt,
            trailing: [
              IconButton(
                onPressed: () {
                  showDialog(
                    context: context,
                    builder: (context) => NewModuleDialog(module: currentNode),
                  );
                },
                icon: Icon(Icons.edit, size: 16),
              ),
              if (node.removable)
                IconButton(
                  onPressed: () {
                    ref
                        .read(currentFlowProvider.notifier)
                        .removeFromFlow(currentNode);
                  },
                  icon: Icon(Icons.remove),
                ),
            ].row(min: true),
          ),
        ).size(width: node.width, height: node.height),
        if (node.output != null)
          Card(
            elevation: 4,
            child: ListTile(title: 'Output'.txt, subtitle: node.output!.txt),
          ).size(width: node.width),
      ],
    );
  }
}
