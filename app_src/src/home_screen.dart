import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:latin_ui/src/events/events_list.dart';
import 'package:latin_ui/src/modules/module_list.dart';
import 'package:latin_ui/src/playground/output_panel.dart';
import 'package:latin_ui/src/playground/playground.dart';
import 'package:smiles/smiles.dart';

///
/// HomeScreen
///
class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
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
          'Latin Playground'.h2,
        ].row(),
      ),
      body: Row(
        mainAxisAlignment: MainAxisAlignment.start,
        mainAxisSize: MainAxisSize.max,
        children: [
          ModuleList().percentOfScreen(width: 0.3).padByUnits(2, 1, 2, 1),
          Playground().percentOfScreen(width: 0.3).padByUnits(2, 1, 2, 1),
          OutputPanel().percentOfScreen(width: 0.3).padByUnits(2, 1, 2, 1),
        ],
      ),
    );
  }
}
