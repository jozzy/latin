import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:latin_ui/src/events/notifiers/fetch_events.dart';
import 'package:latin_ui/src/events/models/event.dart';
import 'package:smiles/smiles.dart';

///
/// EventsList
///
class EventsList extends ConsumerWidget {
  const EventsList({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final eventsAsync = ref.watch(eventsProvider);

    return eventsAsync.when(
      data: (events) => _buildList(events, context, ref).padByUnits(0, 3, 0, 3),
      loading: () =>
          _buildList(List.empty(), context, ref).padByUnits(0, 3, 0, 3),
      error: (err, stack) => Center(child: Text('Error: $err')),
    );
  }

  Widget _buildList(List<Event> events, BuildContext context, WidgetRef ref) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.start,
      mainAxisSize: MainAxisSize.max,
      children: [
        ListTile(
          title: 'Events'.h2,
          subtitle: 'List of events triggered by the flow.'.txt,
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.end,
          children: [
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.deepPurple,
                foregroundColor: Colors.white,
              ),
              onPressed: () {
                ref.read(eventsProvider.notifier).clear();
              },
              child: 'Clear'.style(color: Colors.white),
            ),
          ],
        ),
        ListView.builder(
          itemCount: events.length,
          itemBuilder: (context, index) {
            final event = events[index];
            final eventData = event.data;
            return Card(
              margin: const EdgeInsets.all(8),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  ListTile(
                    leading: Icon(Icons.score_sharp, color: Colors.deepPurple),
                    subtitle: event.event.txt,
                    title:
                        eventData['event']?.toString().txt ?? 'Flow Result'.txt,
                  ),
                  Text(
                    eventData['output'] ?? 'No Output available.',
                  ).padByUnits(0, 2, 2, 2),
                ],
              ),
            );
          },
        ).expand(),
      ],
    );
  }
}
