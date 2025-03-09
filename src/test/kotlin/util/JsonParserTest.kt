package util

import kotlinx.serialization.SerializationException
import model.Command
import model.CommandList
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
 fun `test toJson serializes object to JSON correctly`() {
  // Przygotowanie: przykładowy obiekt CommandList
  val commandList = CommandList(
   commands = listOf(
    Command("addVehicle", "v1", "north", "south"),
    Command("step")
   )
  )

  // Wykonanie: serializacja do JSON-a
  val jsonString = JsonParser.toJson(commandList)

  // Weryfikacja: sprawdzamy, czy JSON jest poprawny
  val expectedJson = """
            {"commands":[{"type":"addVehicle","vehicleId":"v1","startRoad":"north","endRoad":"south"},{"type":"step"}]}
        """.trimIndent()
  assertEquals(expectedJson, jsonString)
 }
}