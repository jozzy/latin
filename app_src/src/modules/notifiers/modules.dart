import 'package:latin_ui/src/modules/models/module.dart';
import 'package:latin_ui/src/modules/services/fetch_modules.dart';
import 'package:latin_ui/src/modules/services/update_module.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'modules.g.dart';

@riverpod
class Modules extends _$Modules {
  @override
  Future<List<Module>> build() async {
    return ref.watch(modulesLoaderProvider).fetch();
  }

  Future<void> updateModule(Module module) async {
    await ref.read(moduleUpdaterProvider).update(module);
    final modules = await ref.read(modulesLoaderProvider).fetch();
    state = AsyncData(modules);
  }
}
