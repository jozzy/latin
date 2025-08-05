import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:latin_ui/src/events/notifiers/fetch_events.dart';
import 'package:latin_ui/src/modules/notifiers/current_flow.dart';
import 'package:smiles/smiles.dart';

class InputPanel extends StatefulWidget {
  const InputPanel({super.key});

  @override
  State<InputPanel> createState() => _InputPanelState();
}

class _InputPanelState extends State<InputPanel> {
  final _textController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Column(
        children: [
          ColoredBox(
            color: Colors.grey[200]!,
            child: Consumer(
              builder: (context, ref, _) {
                final inputTemplate = ref.watch(
                  currentFlowProvider.select(
                    (flow) => flow.isNotEmpty ? flow.first.inputTemplate : null,
                  ),
                );
                _textController.text = inputTemplate ?? '';
                return TextField(
                  style: TextStyle(fontFamily: 'Courier New', fontSize: 16),
                  controller: _textController,
                  decoration: InputDecoration(
                    border: InputBorder.none,
                    contentPadding: const EdgeInsets.all(8),
                  ),
                  onChanged: (text) {},
                  maxLines: null,
                  expands: true,
                  keyboardType: TextInputType.multiline,
                ).padByUnits(2, 2, 2, 2);
              },
            ),
          ).padByUnits(1, 1, 1, 1).expand(),
          VGap(),
          Row(
            children: [
              Spacer(),
              Consumer(
                builder: (context, ref, _) => ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.deepPurple,
                    foregroundColor: Colors.white,
                  ),
                  onPressed: ref.watch(currentFlowProvider).isNotEmpty
                      ? () {
                          ref.read(eventsProvider.notifier).clear();
                          ref
                              .read(currentFlowProvider.notifier)
                              .startFlow(_textController.text);
                        }
                      : null,
                  child: 'Run'.style(color: Colors.white),
                ),
              ),
            ],
          ).padByUnits(2, 1, 2, 1),
        ],
      ).padByUnits(1, 1, 1, 1),
    );
  }

  @override
  dispose() {
    _textController.dispose();
    super.dispose();
  }
}
