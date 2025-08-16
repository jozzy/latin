import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:latin_ui/src/events/models/event.dart';
import 'package:latin_ui/src/events/notifiers/fetch_events.dart';
import 'package:latin_ui/src/modules/notifiers/current_flow.dart';
import 'package:latin_ui/src/core/duration.dart';
import 'package:latin_ui/src/modules/module_dialog.dart';
import 'package:latin_ui/src/modules/notifiers/current_flow.dart';
import 'package:latin_ui/src/modules/notifiers/modules.dart';
import 'package:latin_ui/src/playground/input_panel.dart';
import 'package:smiles/smiles.dart';

class FlowList extends ConsumerWidget {
  const FlowList({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final flow = ref.watch(currentFlowProvider);
    final events = ref.watch(eventsProvider);

    return ListView.builder(
      itemCount: flow.length,
      itemBuilder: (context, index) {
        final module = flow[index];
        final event = events.valueOrNull?.findCompletedFor(module);
        final handoverEvent = events.valueOrNull?.findHandoverFor(
          module,
        );
        final executed = event != null;

        return Card(
          elevation: 0,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              ListTile(
                leading: executed
                    ? Icon(Icons.check_circle, color: Colors.green)
                    : Icon(Icons.circle, color: Colors.grey[300]),
                trailing: IconButton(
                  onPressed: () {
                    ref
                        .read(currentFlowProvider.notifier)
                        .removeFromFlow(flow[index]);
                  },
                  icon: Icon(Icons.remove),
                ),
                title: Text(flow[index].name),
                subtitle: event?.data['moduleId'] != null
                    ? format(event?.data['duration'] as String).txt
                    : Text('Not triggered yet'),
              ),
              if (handoverEvent != null)
                ListTile(
                  dense: true,
                  leading: Icon(
                    Icons.swap_horiz,
                    color: context.colorScheme.primary,
                  ),
                  title: Text(
                    'Redirected to ${handoverEvent.data['toModule'] ?? 'unknown'}',
                  ),
                  trailing: IconButton(
                    onPressed: () {
                      final module = ref
                          .read(modulesProvider)
                          .valueOrNull
                          ?.findFirst(
                            (m) => m.name == handoverEvent.data['toModule'],
                          );
                      if (module != null) {
                        showDialog(
                          context: context,
                          builder: (_) => NewModuleDialog(module: module),
                        );
                      }
                    },
                    icon: Icon(Icons.remove_red_eye_rounded, size: 14),
                  ),
                ),
              if (event != null)
                ExpansionTile(
                  dense: true,
                  tilePadding: const EdgeInsets.all(0),
                  title: Text('Result'),
                  controlAffinity: ListTileControlAffinity.leading,
                  children: <Widget>[
                    (event.data['output']?.toString() ?? '').txt
                        .toLeft()
                        .padding(),
                  ],
                ).padByUnits(0, 0, 0, 2),
            ],
          ),
        );
      },
    );
  }
}
