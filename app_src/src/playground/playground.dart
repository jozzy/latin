import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:latin_ui/src/core/duration.dart';
import 'package:latin_ui/src/core/title_panel.dart';
import 'package:latin_ui/src/events/notifiers/fetch_events.dart';
import 'package:latin_ui/src/modules/module_dialog.dart';
import 'package:latin_ui/src/modules/notifiers/current_flow.dart';
import 'package:latin_ui/src/modules/notifiers/modules.dart';
import 'package:latin_ui/src/playground/input_panel.dart';
import 'package:smiles/smiles.dart';

///
/// Playground
///
class Playground extends StatefulWidget {
  const Playground({super.key});

  @override
  State<Playground> createState() => _PlaygroundState();
}

class _PlaygroundState extends State<Playground> {
  final _iconController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TitlePanel(title: 'Input', subtitle: 'Provide input for your flow.'),
        InputPanel().size(height: 300),
        VGap(),
        Consumer(
          builder: (context, ref, _) {
            final running = ref.watch(runningFlowProvider);
            return TitlePanel(
              title: 'Current Flow',
              subtitle: 'Added modules will appear here.',
              trailing: running.$2
                  ? CircularProgressIndicator().size(width: 20, height: 20)
                  : null,
            );
          },
        ),
        Consumer(
          builder: (context, ref, _) {
            final flow = ref.watch(currentFlowProvider);
            final events = ref.watch(eventsProvider);

            return ListView.builder(
              itemCount: flow.length,
              itemBuilder: (context, index) {
                final module = flow[index];
                final event = events.valueOrNull?.findFirst(
                  (e) =>
                      (e.data['event'] != null &&
                      module.triggers.contains(e.data['event'] as String)),
                );
                final handoverEvent = events.valueOrNull?.findFirst(
                  (e) =>
                      (e.data['fromEvent'] != null &&
                      module.triggers.contains(e.data['fromEvent'] as String)),
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
                        subtitle: event?.data['event'] != null
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
                            'Redirected to ${handoverEvent.data['toEvent'] ?? 'unknown'}',
                          ),
                          trailing: IconButton(
                            onPressed: () {
                              final module = ref
                                  .read(modulesProvider)
                                  .valueOrNull
                                  ?.findFirst(
                                    (m) =>
                                        m.name == handoverEvent.data['toEvent'],
                                  );
                              if (module != null) {
                                showDialog(
                                  context: context,
                                  builder: (_) =>
                                      NewModuleDialog(module: module),
                                );
                              }
                            },
                            icon: Icon(Icons.remove_red_eye_rounded, size: 14,),
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
          },
        ).expand(),
      ],
    );
  }

  @override
  dispose() {
    _iconController.dispose();
    super.dispose();
  }
}
