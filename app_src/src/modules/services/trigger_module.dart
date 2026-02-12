import 'dart:convert';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:http/http.dart' as http;
import 'package:latin_ui/src/modules/models/module.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

class ModulesTrigger {
  Future<String> trigger(String event, String input) async {
    final response = await http.post(
      Uri.parse("http://localhost:8080/trigger/$event"),
      body: input,
    );
    if (response.statusCode == 200) {
      return response.body;
    } else {
      throw Exception('Error triggering event: $event');
    }
  }
}
