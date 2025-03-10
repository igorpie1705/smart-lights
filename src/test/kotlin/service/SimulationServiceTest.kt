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

 // Test 7: Dodawanie dużej liczby pojazdów
 @Test
 fun `test adding large number of vehicles`() {
  val commands = mutableListOf<Command>()
  for (i in 1..100) {
   commands.add(Command("addVehicle", "v$i", "north", "south"))
  }
  simulation.runCommands(commands)

  val intersection = simulation.getIntersection()
  assertEquals(100, intersection.vehicles.size) // Sprawdź, czy wszystkie pojazdy zostały dodane
 }

 // Test 8: Przepuszczanie dużej liczby pojazdów w wielu krokach
 @Test
 fun `test passing large number of vehicles in multiple steps`() {
  val commands = mutableListOf<Command>()
  for (i in 1..50) {
   commands.add(Command("addVehicle", "v$i", "north", "south"))
  }
  commands.add(Command("step"))
  commands.add(Command("step"))

  val stepStatuses = simulation.runCommands(commands)
  assertEquals(2, stepStatuses.size) // Dwa kroki symulacji
  assertTrue(stepStatuses[0].leftVehicles.size <= 2) // W każdym kroku przepuszczane są maksymalnie 2 pojazdy
  assertTrue(stepStatuses[1].leftVehicles.size <= 2)
 }

 // Test 9: Testowanie priorytetów świateł dla różnych kierunków
 @Test
 fun `test traffic light priority for different directions`() {
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "north", "south"),
    Command("addVehicle", "v2", "east", "west"),
    Command("addVehicle", "v3", "south", "north"),
    Command("addVehicle", "v4", "west", "east"),
    Command("step"),
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

 // Test 10: Testowanie zachowania, gdy pojazdy są dodawane z różnych kierunków
 @Test
 fun `test vehicles added from multiple directions`() {
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "north", "south"),
    Command("addVehicle", "v2", "east", "west"),
    Command("addVehicle", "v3", "south", "north"),
    Command("addVehicle", "v4", "west", "east"),
    Command("step")
   )
  )

  val stepStatuses = simulation.runCommands(listOf(Command("step")))
  assertEquals(1, stepStatuses.size)
  assertTrue(stepStatuses[0].leftVehicles.size <= 2) // Maksymalnie 2 pojazdy mogą przejechać na raz
 }

 // Test 11: Testowanie zachowania, gdy pojazdy są dodawane w losowej kolejności
 @Test
 fun `test vehicles added in random order`() {
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "east", "west"),
    Command("addVehicle", "v2", "north", "south"),
    Command("addVehicle", "v3", "west", "east"),
    Command("addVehicle", "v4", "south", "north"),
    Command("step"),
    Command("step")
   )
  )

  val stepStatuses = simulation.runCommands(listOf(Command("step")))
  assertEquals(1, stepStatuses.size)
  assertTrue(stepStatuses[0].leftVehicles.size <= 2) // Maksymalnie 2 pojazdy mogą przejechać na raz
 }

 // Test 12: Testowanie zachowania, gdy pojazdy są dodawane z tym samym kierunkiem startowym i docelowym
 @Test
 fun `test vehicles with same start and end road`() {
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "north", "north"), // Nieprawidłowy kierunek
    Command("addVehicle", "v2", "south", "south")  // Nieprawidłowy kierunek
   )
  )

  val intersection = simulation.getIntersection()
  assertEquals(0, intersection.vehicles.size) // Pojazdy z nieprawidłowymi kierunkami nie powinny być dodane
 }

 // Test 13: Testowanie zachowania, gdy pojazdy są dodawane z nieprawidłowymi kierunkami
 @Test
 fun `test vehicles with invalid directions`() {
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "invalid_start", "south"), // Nieprawidłowy kierunek startowy
    Command("addVehicle", "v2", "north", "invalid_end")    // Nieprawidłowy kierunek docelowy
   )
  )

  val intersection = simulation.getIntersection()
  assertEquals(0, intersection.vehicles.size) // Pojazdy z nieprawidłowymi kierunkami nie powinny być dodane
 }

 // Test 14: Testowanie zachowania, gdy pojazdy są dodawane z przeciwnych kierunków i przepuszczane w wielu krokach
 @Test
 fun `test vehicles from opposite directions in multiple steps`() {
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "north", "south"),
    Command("addVehicle", "v2", "south", "north"),
    Command("addVehicle", "v3", "east", "west"),
    Command("addVehicle", "v4", "west", "east"),
    Command("step"),
    Command("step"),
    Command("step")
   )
  )

  val stepStatuses = simulation.runCommands(listOf(Command("step")))
  assertEquals(1, stepStatuses.size)
  assertTrue(stepStatuses[0].leftVehicles.size <= 2) // Maksymalnie 2 pojazdy mogą przejechać na raz
 }
// Test 15: Testowanie sytuacji, w której dwa samochody mają tę samą (konfliktującą) drogę końcową.
 @Test
 fun `test step with conflicting directions`() {
  val simulation = SimulationService()
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "north", "west"),
    Command("addVehicle", "v2", "south", "west"),
    Command("step")
   )
  )

  val intersection = simulation.getIntersection()
  assertEquals(1, intersection.vehicles.size) // Tylko jeden pojazd powinien pozostać
  assertTrue(intersection.vehicles.any { it.id == "v2" }) // v2 powinien pozostać
 }

 // Test 16: Testowanie sytuacji, gdy samochody nie mają konfliktujących dróg końcowych
 @Test
 fun `test step with non-conflicting directions`() {
  val simulation = SimulationService()
  simulation.runCommands(
   listOf(
    Command("addVehicle", "v1", "north", "west"),
    Command("addVehicle", "v2", "south", "east"),
    Command("step")
   )
  )

  val intersection = simulation.getIntersection()
  assertEquals(0, intersection.vehicles.size) // Oba pojazdy powinny przejechać
 }
}