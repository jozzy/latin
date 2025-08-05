
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:latin_ui/src/core/secondary_button.dart';
import 'package:smiles/smiles.dart';

class DialogHeader extends StatelessWidget {
  const DialogHeader(this.title, {super.key});

  final String title;

  @override
  Widget build(BuildContext context) {
    return ColoredBox(
      color: context.colorScheme.surfaceContainer,
      child: [
        title.h2,
        Spacer(),
        SecondaryButton(
          icon: Icons.close,
          description: 'Close Dialog',
          onPressed: () {
            context.pop();
          },
        ),
      ].row().padByUnits(2, 2, 2, 2),
    );
  }
}
