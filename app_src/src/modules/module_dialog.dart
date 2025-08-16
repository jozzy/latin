import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:latin_ui/src/core/dialog_header.dart';
import 'package:latin_ui/src/modules/models/module.dart';
import 'package:latin_ui/src/modules/notifiers/modules.dart';
import 'package:latin_ui/src/modules/syntax_text_controller.dart';
import 'package:smiles/smiles.dart';

final moduleSyntax = {
  '@tool': Colors.blue[900] ?? Colors.blue,
  '@respond': Colors.blue[900] ?? Colors.blue,
  '#\\w+': Colors.blue[900] ?? Colors.blue,
};

class NewModuleDialog extends StatefulWidget {
  const NewModuleDialog({super.key, this.module});

  final Module? module;

  @override
  NewModuleDialogState createState() => NewModuleDialogState();
}

class NewModuleDialogState extends State<NewModuleDialog> {
  final _nameController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _valueController = SyntaxTextController()..colorText(moduleSyntax);
  final _tagsController = TextEditingController();
  final _inputTemplateController = TextEditingController();

  @override
  void initState() {
    if (widget.module != null) {
      _nameController.text = widget.module!.name;
      _descriptionController.text = widget.module!.description;
      _valueController.text = widget.module!.instructions;
      _inputTemplateController.text = widget.module!.inputTemplate ?? '';
      _tagsController.text = widget.module!.triggers.join(',');
    }
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      titlePadding: const EdgeInsets.all(0),
      backgroundColor: context.colorScheme.surface,
      title: DialogHeader(
        (widget.module != null) ? 'Edit Module' : 'Add New Module',
      ),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            'Name'.style(size: 18, color: context.colorScheme.primary).toLeft(),
            TextField(
              style: TextStyle(fontSize: 16),
              controller: _nameController,
              decoration: InputDecoration(hintText: 'The name of the module'),
            ),
            VGap(),
            VGap.small(),
            'Description'
                .style(size: 16, color: context.colorScheme.primary)
                .toLeft(),
            TextField(
              controller: _descriptionController,
              decoration: InputDecoration(
                hintText: 'A simple description of the module.',
              ),
            ),
            VGap(),
            VGap.small(),
            'Triggers'
                .style(size: 16, color: context.colorScheme.primary)
                .toLeft(),
            TextField(
              controller: _tagsController,
              decoration: InputDecoration(
                hintText: 'A comma-separated list of triggers for the module.',
              ),
            ),
            VGap(),
            VGap.small(),
            'Input'
                .style(size: 16, color: context.colorScheme.primary)
                .toLeft(),
            ExpansionTile(
              dense: true,
              tilePadding: const EdgeInsets.all(0),
              title: Text('Input Template'),
              subtitle: Text(
                'Use this template to define the input for the module.',
              ),
              controlAffinity: ListTileControlAffinity.leading,
              children: <Widget>[
                TextField(
                  controller: _inputTemplateController,
                  maxLines: 9,
                  minLines: 3,
                ),
              ],
            ),
            VGap(),
            VGap.small(),
            'Instructions'
                .style(size: 16, color: context.colorScheme.primary)
                .toLeft(),
            VGap(),
            TextField(controller: _valueController, maxLines: 18, minLines: 12),
          ],
        ).percentOfScreen(width: 0.4),
      ),
      actions: <Widget>[
        TextButton(
          child: const Text('Cancel'),
          onPressed: () => Navigator.of(context).pop(),
        ),
        HGap(),
        Consumer(
          builder: (context, ref, child) => TextButton(
            style: TextButton.styleFrom(
              foregroundColor: context.colorScheme.onPrimary,
              backgroundColor: context.colorScheme.primary,
            ),
            child: 'Update'.txt,
            onPressed: () {
              ref
                  .read(modulesProvider.notifier)
                  .updateModule(
                    Module(
                      name: _nameController.text,
                      version: widget.module?.version ?? '1.0.0',
                      description: _descriptionController.text,
                      instructions: _valueController.text,
                      inputTemplate: _inputTemplateController.text.isNotEmpty
                          ? _inputTemplateController.text
                          : null,
                      triggers: _tagsController.text
                          .split(',')
                          .map((e) => e.trim())
                          .toSet(),
                    ),
                  );
              context.pop();
            },
          ).size(width: 120),
        ),
      ],
    );
  }

  @override
  void dispose() {
    _nameController.dispose();
    _descriptionController.dispose();
    _valueController.dispose();
    _tagsController.dispose();
    _inputTemplateController.dispose();
    super.dispose();
  }
}
