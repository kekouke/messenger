package com.kekouke.tfsspring.data.api.response.events

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class EventDtoTypeAdapter : JsonDeserializer<EventResponseBase> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): EventResponseBase {
        val jsonObject = json.asJsonObject

        return when (val type = jsonObject[EVENT_TYPE].asString) {
            MESSAGE_EVENT_TYPE -> {
                context.deserialize(jsonObject, EventResponseBase.MessageEvent::class.java)
            }

            REACTION_EVENT_TYPE -> {
                context.deserialize(jsonObject, EventResponseBase.ReactionEvent::class.java)
            }

            STREAM_EVENT_TYPE -> {
                context.deserialize(jsonObject, EventResponseBase.StreamEvent::class.java)
            }

            else -> {
                EventResponseBase.UnknownEvent(jsonObject[ID].asInt, type)
            }
        }
    }

    companion object {
        private const val MESSAGE_EVENT_TYPE = "message"
        private const val REACTION_EVENT_TYPE = "reaction"
        private const val STREAM_EVENT_TYPE = "stream"
        private const val EVENT_TYPE = "type"
        private const val ID = "id"
    }
}