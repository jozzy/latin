
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:latin_ui/src/routes.dart';
import 'package:latin_ui/src/theme_notifier.dart';
import 'package:latin_ui/src/themes.dart';

class LatinApp extends ConsumerWidget {
  const LatinApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final appRoutes = createRouter(ref);
    return MaterialApp.router(
      title: 'Latin Playground',
      routerConfig: appRoutes,
      debugShowCheckedModeBanner: false,
      theme: lightTheme,
      darkTheme: darkTheme,
      themeMode: ref.watch(themeNotifierProvider),
    );
  }
}
