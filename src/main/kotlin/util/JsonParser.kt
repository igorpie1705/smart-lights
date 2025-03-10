package util

import kotlinx.serialization.json.Json
import model.CommandList
import model.StepStatus

object JsonParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parseCommands(jsonString: String): CommandList {
        return json.decodeFromString(jsonString)
    }

    fun toJson(commandList: List<StepStatus>): String {
        return json.encodeToString(commandList)
    }
}