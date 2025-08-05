import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:latin_ui/src/core/secondary_button.dart';
import 'package:smiles/smiles.dart';

class TitlePanel extends StatelessWidget {
  const TitlePanel({
    super.key,
    required this.title,
    required this.subtitle,
    this.trailing,
  });

  final String title;
  final String subtitle;
  final Widget? trailing;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      dense: true,
      leading: Container(
        width: 8,
        color: vibrantColors[title.hashCode % 5],
        height: 40,
      ),
      title: title.h2,
      subtitle: subtitle.txt,
      trailing: trailing,
    ).padByUnits(0, 0, 1, 0);
  }
}

// list 5 fibrant colors
List<Color> get vibrantColors => [
  Colors.red,
  Colors.green,
  Colors.blue,
  Colors.orange,
  Colors.purple,
];
