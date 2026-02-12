package org.latin.server.events.storage

import org.eclipse.lmos.arc.core.Failure
import org.eclipse.lmos.arc.core.Result
import org.eclipse.lmos.arc.core.Success

/**
 * In-Memory-Implementierung von [ExecutionStorage].
 *
 * Speichert [ExecutionData] Objekte in einer synchronisierten Liste im Speicher.
 * Diese Implementierung ist nicht persistent und eignet sich für Tests oder temporäre Speicherung.
 */
class InMemoryExecutionStorage : ExecutionStorage {
    private val storage = mutableListOf<ExecutionData>()

    override suspend fun save(data: ExecutionData) {
        synchronized(storage) {
            storage.add(data)
        }
    }

    override suspend fun fetch(limit: Int): Result<Executions, StorageException> {
        return try {
            val result = synchronized(storage) {
                storage.takeLast(limit)
            }
            Success(Executions(limit, result))
        } catch (e: Exception) {
            Failure(StorageException("Failed to fetch execution data", e))
        }
    }
}
