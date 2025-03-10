package util

import kotlinx.serialization.SerializationException
import model.Command
import model.CommandList
import model.StepStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JsonParserTest {

    @Test
    fun `test parseCommands parses JSON correctly`() {
        // Przygotowanie: przykładowy JSON
        val jsonString = """
            {
                "commands": [
                    { "type": "addVehicle", "vehicleId": "v1", "startRoad": "north", "endRoad": "south" },
                    { "type": "addVehicle", "vehicleId": "v2", "startRoad": "south", "endRoad": "north" },
                    { "type": "step" }
                ]
            }
        """.trimIndent()

        // Wykonanie: parsowanie JSON-a
        val commandList = JsonParser.parseCommands(jsonString)

        // Weryfikacja: sprawdzamy, czy dane zostały poprawnie sparsowane
        assertEquals(3, commandList.commands.size)
        assertEquals("addVehicle", commandList.commands[0].type)
        assertEquals("v1", commandList.commands[0].vehicleId)
        assertEquals("north", commandList.commands[0].startRoad)
        assertEquals("south", commandList.commands[0].endRoad)
        assertEquals("step", commandList.commands[2].type)
    }

    @Test
    fun `test parseCommands throws exception for invalid JSON`() {
        // Przygotowanie: niepoprawny JSON
        val invalidJsonString = """
            {
                "commands": [
                    { "type": "addVehicle", "vehicleId": "v1", "startRoad": "north" }, // brak "endRoad"
                    { "type": "step" }
                ]
            }
        """.trimIndent()

        // Wykonanie i weryfikacja: oczekujemy wyjątku SerializationException
        assertThrows(SerializationException::class.java) {
            JsonParser.parseCommands(invalidJsonString)
        }
    }

    @Test
    fun `test toJson serializes stepStatuses to JSON correctly`() {
        // Przygotowanie: przykładowa lista StepStatus
        val stepStatuses = listOf(
            StepStatus(listOf("vehicle1", "vehicle2")),
            StepStatus(emptyList()),
            StepStatus(emptyList()),
            StepStatus(listOf("vehicle3")),
            StepStatus(listOf("vehicle4"))
        )

        // Wykonanie: serializacja do JSON-a
        val jsonString = JsonParser.toJson(stepStatuses)

        // Weryfikacja: porównanie z oczekiwanym JSON-em
        val expectedJson = """[{"leftVehicles":["vehicle1","vehicle2"]},{"leftVehicles":[]},{"leftVehicles":[]},{"leftVehicles":["vehicle3"]},{"leftVehicles":["vehicle4"]}]"""

        assertEquals(expectedJson, jsonString)
    }
}

