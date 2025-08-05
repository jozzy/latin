import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:latin_ui/src/core/title_panel.dart';
import 'package:latin_ui/src/events/notifiers/fetch_events.dart';
import 'package:latin_ui/src/modules/notifiers/current_flow.dart';
import 'package:smiles/smiles.dart';

class OutputPanel extends StatefulWidget {
  const OutputPanel({super.key});

  @override
  State<OutputPanel> createState() => _OutputPanelState();
}

class _OutputPanelState extends State<OutputPanel> {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TitlePanel(title: 'Output', subtitle: 'The output of the Flow'),
        Card(
          child: ColoredBox(
            color: Colors.grey[200]!,
            child: SingleChildScrollView(
              child: Consumer(
                builder: (context, ref, _) {
                  final result = ref.watch(
                    eventsProvider.select((events) {
                      return events.valueOrNull?.findFirst((event) {
                        return event.event == 'FlowCompletedEvent';
                      });
                    }),
                  );
                        
                  return (result == null
                          ? ''
                          : (result.data['output']?.toString() ?? ''))
                      .h3
                      .padByUnits(2, 2, 2, 2);
                },
              ),
            ),
          ).padByUnits(2, 2, 2, 2).size(width: double.infinity),
        ).expand()
      ],
    );
  }
}
