package com.kekouke.tfsspring.data.api

import android.util.Log
import com.kekouke.tfsspring.data.api.response.events.EventResponseBase
import com.kekouke.tfsspring.data.api.services.LongPollingApiService
import com.kekouke.tfsspring.runCatchingNonCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.SocketTimeoutException
import java.util.concurrent.CancellationException
import javax.inject.Inject

private const val SERVER_POLL_RETRY_DELAY_IN_MILLISECONDS = 5000L
private const val TAG = "EventManager"

class EventManager @Inject constructor(private val api: LongPollingApiService) {

    suspend fun registerEventQueue(eventTypes: String): Result<Flow<EventResponseBase>> {
        val result = runCatchingNonCancellation {
            api.registerEventQueue(eventTypes)
        }

        return result.fold(
            onSuccess = { response ->
                Result.success(getEventsFlow(response.queueId, response.lastEventId))
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }

    private fun getEventsFlow(queueId: String, eventId: Int) = flow {
        var lastEventId = eventId

        while (true) {
            runWithCatchPollExceptions {
                val response = api.getEvents(queueId, lastEventId)

                response.events.forEach { event ->
                    when (event) {
                        is EventResponseBase.UnknownEvent -> Unit
                        else -> emit(event)
                    }

                    lastEventId = event.id
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    private suspend inline fun runWithCatchPollExceptions(poll: () -> Unit) = try {
        poll()
    } catch (e: CancellationException) {
        throw e
    } catch (_: SocketTimeoutException) {
    } catch (exception: Exception) {
        Log.d(TAG, exception.toString())
        delay(SERVER_POLL_RETRY_DELAY_IN_MILLISECONDS)
    }
}