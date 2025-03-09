package util

import kotlinx.serialization.json.Json
import model.CommandList
import model.StepStatus

object JsonParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parseCommands(jsonString: String): CommandList {
        return json.decodeFromString(jsonString)
    }

    fun toJson(stepStatuses: List<StepStatus>): String {
        val wrapper = mapOf("stepStatuses" to stepStatuses)
        return json.encodeToString(wrapper)
    }

}