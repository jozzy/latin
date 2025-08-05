import 'dart:convert';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:latin_ui/src/events/models/event.dart';
import 'package:latin_ui/src/modules/notifiers/current_flow.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:flutter_client_sse/constants/sse_request_type_enum.dart';
import 'package:flutter_client_sse/flutter_client_sse.dart';

part 'fetch_events.g.dart';

@Riverpod(keepAlive: true)
class Events extends _$Events {
  var allMessages = const <Event>[];

  @override
  Stream<List<Event>> build() async* {
    ref.onDispose(SSEClient.unsubscribeFromSSE);

    await for (final message in SSEClient.subscribeToSSE(
      header: {},
      method: SSERequestType.GET,
      url: 'http://localhost:8080/events',
    )) {
      final data = json.decode(message.data!);
      final String? runningFlow = ref.read(runningFlowProvider).$1;

      if (data['correlationId'] == runningFlow) {
        allMessages = [
          Event(id: message.id!, event: message.event!, data: data),
          ...allMessages,
        ];
        yield allMessages;
      }
    }
  }

  void clear() {
    allMessages = const <Event>[];
    state = AsyncData(allMessages);
  }
}
