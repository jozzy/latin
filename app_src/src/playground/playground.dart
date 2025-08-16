import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:latin_ui/src/core/duration.dart';
import 'package:latin_ui/src/core/title_panel.dart';
import 'package:latin_ui/src/modules/notifiers/current_flow.dart';
import 'package:latin_ui/src/modules/notifiers/modules.dart';
import 'package:latin_ui/src/playground/flow_list.dart';
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
              trailing: [
                IconButton(
                  onPressed: () {
                    context.push("/graph");
                  },
                  icon: Icon(Icons.open_in_new),
                ),
                if (running.$2) HGap(),
                if (running.$2)
                  CircularProgressIndicator().size(width: 20, height: 20),
              ].row(min: true),
            );
          },
        ),
        FlowList().expand(),
      ],
    );
  }

  @override
  dispose() {
    _iconController.dispose();
    super.dispose();
  }
}
