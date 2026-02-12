import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:latin_ui/src/graph/graph_screen.dart';
import 'package:latin_ui/src/home_screen.dart';

final GlobalKey<NavigatorState> _rootNavigatorKey = GlobalKey<NavigatorState>(
  debugLabel: 'root',
);
final GlobalKey<NavigatorState> _mainNavigatorKey = GlobalKey<NavigatorState>(
  debugLabel: 'mainNav',
);

/// Main Routing
GoRouter createRouter(WidgetRef ref) {
  return GoRouter(
    initialLocation: '/home',
    // debugLogDiagnostics: true,
    navigatorKey: _rootNavigatorKey,
    routes: [
      GoRoute(path: '/home', builder: (context, state) => HomeScreen()),
      GoRoute(path: '/graph', builder: (context, state) => GraphScreen()),
    ],
  );
}
