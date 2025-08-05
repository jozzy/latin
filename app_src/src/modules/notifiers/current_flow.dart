import 'dart:convert';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:http/http.dart' as http;
import 'package:latin_ui/src/events/notifiers/fetch_events.dart';
import 'package:latin_ui/src/modules/models/module.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:smiles/smiles.dart';

part 'current_flow.g.dart';

final runningFlowProvider = StateProvider<(String?, bool)>(
  (ref) => (null, false),
);

@riverpod
class CurrentFlow extends _$CurrentFlow {
  @override
  List<Module> build() {
    ref.listen(eventsProvider, (_, events) {
      if (events.valueOrNull != null) {
        final runningFlow = ref.read(runningFlowProvider).$1;
        if (runningFlow != null) {
          final event = events.valueOrNull!.findFirst(
            (e) =>
                e.data['correlationId'] == runningFlow &&
                e.event == 'FlowCompletedEvent',
          );
          if (event != null) {
            ref.read(runningFlowProvider.notifier).state = (runningFlow, false);
          }
        }
      }
    });
    return List.empty();
  }

  addToFlow(Module module) {
    state = [...state, module];
  }

  removeFromFlow(Module module) {
    state = state.where((m) => m.name != module.name).toList();
  }

  bool hasFlow() {
    return state.isNotEmpty;
  }

  startFlow(String input) async {
    print('Starting flow with input: $input');
    final steps = state.map((s) => s.triggers.firstOrNull).toList();

    try {
      final result = await http.post(
        Uri.parse("http://localhost:8080/flows"),
        headers: {},
        body: json.encode({
          "id": 'playground_flow',
          "name": 'Playground Flow',
          "description": 'Playground flow',
          "steps": steps,
        }),
      );

      print('Flow creation result: ${result.statusCode}');
      if (result.statusCode == 201) {
        print(result.body);
      } else {
        throw Exception('Error starting flow ${result.statusCode}');
      }

      final response = await http.post(
        Uri.parse("http://localhost:8080/start/playground_flow"),
        headers: {},
        body: input,
      );

      if (response.statusCode == 201) {
        final data = json.decode(response.body) as Map<String, dynamic>;
        ref.read(runningFlowProvider.notifier).state = (
          data['correlationId'] as String?,
          true,
        );
      } else {
        throw Exception('Error starting flow ${response.statusCode}');
      }
    } catch (e) {
      print('Error creating flow: $e');
      throw Exception('Error creating flow');
    }
  }
}
