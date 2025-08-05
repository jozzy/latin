import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:latin_ui/src/core/duration.dart';
import 'package:latin_ui/src/core/text.dart';
import 'package:latin_ui/src/core/title_panel.dart';
import 'package:latin_ui/src/modules/module_dialog.dart';
import 'package:latin_ui/src/modules/notifiers/current_flow.dart';
import 'package:latin_ui/src/modules/notifiers/modules.dart';
import 'package:latin_ui/src/modules/search_panel.dart';
import 'package:smiles/smiles.dart';

///
/// ModuleList
///

final modulesFilterProvider = StateProvider<String?>((ref) => null);

class ModuleList extends ConsumerWidget {
  const ModuleList({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final modulesAsync = ref.watch(modulesProvider);
    final selected = ref.watch(currentFlowProvider);
    final filter = ref.watch(modulesFilterProvider);

    return modulesAsync.when(
      data: (m) {
        // Apply filter if provided
        var modules = m.toList();
        if (filter != null) {
          modules = modules
              .where(
                (module) =>
                    module.name.toLowerCase().contains(filter.toLowerCase()),
              )
              .toList();
        }
        return Column(
          mainAxisAlignment: MainAxisAlignment.start,
          mainAxisSize: MainAxisSize.max,
          children: [
            TitlePanel(
              title: 'Modules',
              subtitle: 'Select a module to add it to the flow.',
            ),
            SearchPanel().padByUnits(0, 1, 0, 1),
            VGap(),
            VGap(),
            VGap(),
            Divider().padByUnits(0, 1, 0, 1),
            VGap(),
            ListView.builder(
              itemCount: modules.length,
              itemBuilder: (context, index) {
                final module = modules[index];
                return Card(
                  margin: const EdgeInsets.all(8),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      ListTile(
                        trailing: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            IconButton(
                              onPressed: () {
                                showDialog(
                                  context: context,
                                  builder: (context) =>
                                      NewModuleDialog(module: module),
                                );
                              },
                              icon: Icon(Icons.edit, size: 16),
                            ),
                            if (module.triggers.isNotEmpty)
                              selected.findFirst(
                                        (s) => s.name == module.name,
                                      ) !=
                                      null
                                  ? Icon(
                                      Icons.check_circle,
                                      color: Colors.green[900],
                                    )
                                  : IconButton(
                                      icon: const Icon(Icons.add),
                                      onPressed: () {
                                        ref
                                            .read(currentFlowProvider.notifier)
                                            .addToFlow(module);
                                      },
                                    ),
                          ],
                        ),
                        leading: Icon(
                          Icons.settings,
                          color: context.colorScheme.primary,
                        ),
                        title: Text(
                          module.name,
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            // color: Colors.deepPurpleAccent[700],
                          ),
                        ),
                        subtitle: Text(
                          (module.description?.isNotEmpty == true)
                              ? module.description!
                              : 'No description available',
                        ),
                      ),
                      Divider().padByUnits(0, 1, 0, 1),
                      VGap(),
                      if (module.timer != null)
                        Text(
                          'Every',
                          style: TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.bold,
                            // color: Colors.deepPurpleAccent[700],
                          ),
                        ).toLeft().padByUnits(0, 2, 0, 2),
                      if (module.timer != null)
                        format(
                          module.timer!.interval,
                        ).txt.toLeft().padByUnits(0, 2, 0, 2),
                      if (module.timer != null) VGap(),
                      if (module.timer != null) VGap(),
                      Text(
                        'Triggers',
                        style: TextStyle(
                          fontSize: 14,
                          fontWeight: FontWeight.bold,
                          //   color: Colors.deepPurpleAccent[700],
                        ),
                      ).toLeft().padByUnits(0, 2, 0, 2),
                      VGap(),
                      VGap(),
                      module.triggers.isEmpty
                          ? 'No triggers available'.small.toLeft().padByUnits(
                              0,
                              2,
                              2,
                              2,
                            )
                          : Wrap(
                              spacing: 2,
                              children: module.triggers
                                  .map(
                                    (trigger) => Card(
                                      elevation: 0,
                                      color: Colors.deepPurple[50],
                                      child: Text(
                                        trigger,
                                      ).padByUnits(1, 1, 1, 1),
                                    ),
                                  )
                                  .toList(),
                            ).toLeft().padByUnits(0, 0, 0, 1),
                      VGap(),
                      VGap(),
                      Text(
                        'Instructions',
                        style: TextStyle(
                          fontSize: 14,
                          fontWeight: FontWeight.bold,
                          //     color: Colors.deepPurpleAccent[700],
                        ),
                      ).toLeft().padByUnits(0, 2, 0, 2),
                      if (module.instructions != null)
                        module.instructions!
                            .replaceAll("\n", "\n\n")
                            .markDown()
                            .toLeft()
                            .padByUnits(0, 2, 2, 2),
                    ],
                  ),
                );
              },
            ).expand(),
          ],
        );
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (err, stack) => Center(child: Text('Error: $err')),
    );
  }
}
