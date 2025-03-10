package service

import main.model.TrafficLightState
import model.*

class SimulationService {
    private val cycleDuration = 5 // oznacza ile maksymalnie kroków może zostać wykonanych przed zmianą świateł
    private val intersection = Intersection(
        trafficLights = listOf(
            TrafficLight("north", TrafficLightState.GREEN),
            TrafficLight("south", TrafficLightState.GREEN),
            TrafficLight("east", TrafficLightState.RED),
            TrafficLight("west", TrafficLightState.RED)
        ),
        vehicles = mutableListOf(),
        currentCycleDuration = 0
    )

    fun addVehicle(vehicle: Vehicle) {
        intersection.vehicles.add(vehicle)
    }

    private fun step(): List<String> {

        // KROK 1: inicjujemy listę samochodów, które potencjalnie opuszczą skrzyżowanie,
        // oraz sprawdzamy, która droga jest w tym kroku aktywna.
        val leftVehicles = mutableListOf<String>()
        val (dir1, dir2) = intersection.getActiveDirectionPair() ?: return emptyList()

        // KROK 2: sprawdzamy, czy droga jest pusta, jeśli tak to zmienimy światła.
        val dir1HasVehicles = intersection.vehicles.any { it.startRoad == dir1 }
        val dir2HasVehicles = intersection.vehicles.any { it.startRoad == dir2 }

        // KROK 3: puszczamy po jednym pojeździe z każdego kierunku, jeśli są
        val vehicleFromDir1 = intersection.vehicles.firstOrNull { it.startRoad == dir1 }
        val vehicleFromDir2 = intersection.vehicles.firstOrNull { it.startRoad == dir2 }

        // Sprawdzamy, czy oba pojazdy chcą jechać w ten sam kierunek
        if (vehicleFromDir1 != null && vehicleFromDir2 != null && vehicleFromDir1.endRoad == vehicleFromDir2.endRoad) {
            // Tylko jeden pojazd może przejechać (priorytet dla dir1)
            leftVehicles.add(vehicleFromDir1.id)
            intersection.vehicles.remove(vehicleFromDir1)
        } else {
            // Jeśli nie ma konfliktu, puszczamy oba pojazdy
            vehicleFromDir1?.let {
                leftVehicles.add(it.id)
                intersection.vehicles.remove(it)
            }
            vehicleFromDir2?.let {
                leftVehicles.add(it.id)
                intersection.vehicles.remove(it)
            }
        }

        // KROK 4: Zmieniamy światła, jeżeli nie przejechał żaden samochód, lub jeśli przekroczono dopuszczalną długość cyklu.
        if ((!dir1HasVehicles && !dir2HasVehicles) || intersection.currentCycleDuration >= cycleDuration) {
            intersection.changeLights()
            intersection.currentCycleDuration = 0
        }

        // KROK 5: zwracamy ID pojazdów, które przejechały (sortujemy, aby wyniki były deterministyczne)
        return leftVehicles.sorted()
    }

    fun runCommands(commands: List<Command>): List<StepStatus> {
        val stepStatuses = mutableListOf<StepStatus>()

        for (command in commands) {
            try {
                when (command.type) {
                    "addVehicle" -> {
                        // Walidacja pól komendy
                        if (command.vehicleId == null || command.startRoad == null || command.endRoad == null) {
                            println("Brakujące pola w komendzie addVehicle: $command")
                            continue // Pomijamy nieprawidłową komendę
                        }

                        // Walidacja kierunków
                        if (!isValidDirection(command.startRoad) || !isValidDirection(command.endRoad)) {
                            println("Nieprawidłowe kierunki w komendzie addVehicle: $command")
                            continue // Pomijamy nieprawidłową komendę
                        }

                        // Sprawdzenie, czy startRoad i endRoad są różne
                        if (command.startRoad == command.endRoad) {
                            println("Kierunki startowy i docelowy są takie same w komendzie addVehicle: $command")
                            continue // Pomijamy nieprawidłową komendę
                        }

                        // Dodanie pojazdu
                        addVehicle(
                            Vehicle(
                                id = command.vehicleId,
                                startRoad = command.startRoad,
                                endRoad = command.endRoad
                            )
                        )
                    }
                    "step" -> {
                        intersection.currentCycleDuration += 1
                        val leftVehicles = step()
                        stepStatuses.add(StepStatus(leftVehicles))
                    }
                    else -> {
                        println("Nieznany typ komendy: ${command.type}")
                        continue // Pomijamy nieznaną komendę
                    }
                }
            } catch (e: Exception) {
                println("Błąd podczas wykonywania komendy: $command. Błąd: ${e.message}")
            }
        }

        return stepStatuses
    }

    // Funkcja pomocnicza do walidacji kierunków
    private fun isValidDirection(direction: String): Boolean {
        return direction in listOf("north", "south", "east", "west")
    }

    fun getIntersection(): Intersection {
        return intersection
    }
}
