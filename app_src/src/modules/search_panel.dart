import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:latin_ui/src/modules/module_list.dart';
import 'package:smiles/smiles.dart';

///
/// Search Panel
///
class SearchPanel extends ConsumerStatefulWidget {
  const SearchPanel({super.key});

  @override
  SearchPanelState createState() => SearchPanelState();
}

class SearchPanelState extends ConsumerState<SearchPanel> {
  final _textController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final cleared = ref.watch(modulesFilterProvider.select((e) => e == null));

    if (cleared) {
      _textController.clear();
    }

    return SearchBar(
      controller: _textController,
      textStyle: WidgetStatePropertyAll<TextStyle>(theme.textTheme.bodyMedium!),
      constraints: BoxConstraints(maxHeight: 80),
      padding: WidgetStatePropertyAll<EdgeInsetsGeometry>(EdgeInsets.all(4)),
      onChanged: (text) {
        final state = text.trim().isEmpty ? null : text;
        ref.read(modulesFilterProvider.notifier).state = state;
      },
      leading: const Icon(Icons.search, size: 20).padByUnits(0, 1, 0, 1),
    );
  }

  @override
  void dispose() {
    _textController.dispose();
    super.dispose();
  }
}
