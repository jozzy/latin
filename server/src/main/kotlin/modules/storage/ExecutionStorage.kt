package org.latin.server.modules.storage

import kotlinx.serialization.Serializable
import org.eclipse.lmos.arc.core.Result
import org.latin.server.modules.LatinModule
import java.time.Instant

/**
 * Interface for execution storage.
 *
 * This interface defines the contract for storing and retrieving execution data.
 * It can be implemented to provide different storage mechanisms, such as in-memory,
 * file-based, or database storage.
 */
interface ExecutionStorage {

    /**
     * Saves the execution data.
     *
     * @param data The execution data to save.
     */
    suspend fun save(data: ExecutionData)

    /**
     * Fetches the latest execution data up to the specified limit.
     *
     * @param limit The maximum number of execution data entries to fetch.
     * @return A Result containing a list of ExecutionData or an AgentFailedException if the operation fails.
     */
    suspend fun fetch(limit: Int): Result<Executions, StorageException>

}

@Serializable
data class ExecutionData(
    val agent: String,
    val module: LatinModule,
    val input: String,
    val output: String,
    val timestamp: String = Instant.now().toString(),
)

@Serializable
data class Executions(
    val limit: Int,
    val result: List<ExecutionData>,
)

class StorageException(message: String, cause: Throwable? = null) : Exception(message, cause)