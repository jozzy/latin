import 'dart:convert';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:http/http.dart' as http;
import 'package:latin_ui/src/modules/models/module.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'fetch_modules.g.dart';

@riverpod
ModulesLoader modulesLoader(Ref ref) {
  return ModulesLoader();
}

class ModulesLoader {
  Future<List<Module>> fetch() async {
    final response = await http.get(Uri.parse("http://localhost:8080/modules"));
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      print("Modules loaded: ${data}");
      return data.map((json) => Module.fromJson(json)).toList();
    } else {
      throw Exception('Fehler beim Laden der Module');
    }
  }
}
