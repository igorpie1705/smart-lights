package util

import kotlinx.serialization.json.Json
import model.CommandList

object JsonParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parseCommands(jsonString: String): CommandList {
    return json.decodeFromString(jsonString)
    }

    fun toJson(obj: Any): String {
        return json.encodeToString(obj)
    }
}