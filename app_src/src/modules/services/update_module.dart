import 'dart:convert';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:http/http.dart' as http;
import 'package:latin_ui/src/modules/models/module.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'update_module.g.dart';

@riverpod
ModuleUpdater moduleUpdater(Ref ref) {
  return ModuleUpdater();
}

class ModuleUpdater {
  update(Module module) async {
    try {
      final response = await http.put(
        Uri.parse("http://localhost:8080/modules"),
        headers: {},
        body: json.encode(module.toJson()),
      );
      print("Modules updated: ${response.statusCode}");
    } catch (e) {
      print("Error updating module: $e");
    }
  }
}
