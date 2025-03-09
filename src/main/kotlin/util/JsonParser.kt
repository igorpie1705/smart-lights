package util

import kotlinx.serialization.json.Json
import model.CommandList

object JsonParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parseCommands(jsonString: String): CommandList {
        return json.decodeFromString(jsonString)
    }

    fun toJson(commandList: CommandList): String {
        return json.encodeToString(commandList)
    }
}