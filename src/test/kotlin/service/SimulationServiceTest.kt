package service

import main.model.TrafficLightState
import model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SimulationServiceTest {
 private lateinit var simulation: SimulationService

 @BeforeEach
 fun setUp() {
  simulation = SimulationService()
 }

 // Test 1: Dodawanie pojazdów
 @Test
 fun `test adding vehicles`() {
  simulation.addVehicle(Vehicle("v1", "north", "south"))
  simulation.addVehicle(Vehicle("v2", "south", "north"))

  val stepStatuses = simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "north", "south"),
    Command("addVehicle", "v2", "south", "north")
   )
  )

  assertEquals(0, stepStatuses.size) // Brak kroków symulacji, tylko dodanie pojazdów
 }

 // Test 2: Przepuszczanie pojazdów w kolejności FIFO
 @Test
 fun `test vehicles are passed in FIFO order`() {
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "north", "south"),
    Command("addVehicle", "v2", "north", "south"),
   )
  )

  val stepStatuses = simulation.runCommands(listOf(Command("step")))
  assertEquals(1, stepStatuses.size)
  assertEquals(listOf("v1"), stepStatuses[0].leftVehicles) // Tylko jeden pojazd powinien przejechać na raz
 }
 // Test 3: Zmiana świateł po określonej liczbie kroków
 @Test
 fun `test light change after intensity steps`() {
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "north", "south"),
    Command("step"),
    Command("step"),
    Command("step"), // intensity = 3
    Command("step") // now change lights
   )
  )

  val intersection = simulation.getIntersection()
  assertTrue(intersection.trafficLights.any { it.direction == "north" && it.state == TrafficLightState.RED })
  assertTrue(intersection.trafficLights.any { it.direction == "south" && it.state == TrafficLightState.RED })
  assertTrue(intersection.trafficLights.any { it.direction == "east" && it.state == TrafficLightState.GREEN })
  assertTrue(intersection.trafficLights.any { it.direction == "west" && it.state == TrafficLightState.GREEN })
 }

 // Test 4: Zmiana świateł, gdy na drodze nie ma pojazdów
 @Test
 fun `test light change when no vehicles are present`() {
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "north", "south"),
    Command("step"), // Przepuść pojazd
    Command("step")  // Brak pojazdów, zmiana świateł
   )
  )

  val intersection = simulation.getIntersection()
  assertTrue(intersection.trafficLights.any { it.direction == "north" && it.state == TrafficLightState.RED })
  assertTrue(intersection.trafficLights.any { it.direction == "south" && it.state == TrafficLightState.RED })
  assertTrue(intersection.trafficLights.any { it.direction == "east" && it.state == TrafficLightState.GREEN })
  assertTrue(intersection.trafficLights.any { it.direction == "west" && it.state == TrafficLightState.GREEN })
 }


 // Test 5: Bezpieczeństwo - brak konfliktów
 @Test
 fun `test no conflicting green lights`() {
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "north", "south"),
    Command("addVehicle", "v2", "east", "west"),
    Command("step")
   )
  )

  val intersection = simulation.getIntersection()
  val greenLights = intersection.trafficLights.filter { it.state == TrafficLightState.GREEN }
  assertEquals(2, greenLights.size) // Tylko jedna para kierunków ma zielone światło
  assertTrue(
   (greenLights.any { it.direction == "north" } && greenLights.any { it.direction == "south" }) ||
           (greenLights.any { it.direction == "east" } && greenLights.any { it.direction == "west" })
  )
 }
 // Test 6: Przepuszczanie pojazdów z przeciwnych kierunków
 @Test
 fun `test passing vehicles from opposite directions`() {
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "north", "south"),
    Command("addVehicle", "v2", "south", "north"),
   )
  )

  val stepStatuses = simulation.runCommands(listOf(Command("step")))
  assertEquals(1, stepStatuses.size)
  assertEquals(listOf("v1", "v2"), stepStatuses[0].leftVehicles)
 }
}