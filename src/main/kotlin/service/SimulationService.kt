package service

import main.model.TrafficLightState
import model.*

class SimulationService {
    private val cycleDuration = 3
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
        // oraz sprawdzamy która droga jest w tym kroku aktywna.
        val leftVehicles = mutableListOf<String>()
        val (dir1, dir2) = intersection.getActiveDirectionPair() ?: return emptyList()

        // KROK 2: sprawdzamy, czy droga jest pusta, jeśli tak to zmienimy światła.
        val dir1HasVehicles = intersection.vehicles.any { it.startRoad == dir1 }
        val dir2HasVehicles = intersection.vehicles.any { it.startRoad == dir2 }


        // KROK 3: puszczamy po jednym pojeździe z każdego kierunku, jeśli są
        val vehicleFromDir1 = intersection.vehicles.firstOrNull { it.startRoad == dir1 }
        vehicleFromDir1?.let {
            leftVehicles.add(it.id)
            intersection.vehicles.remove(it)
        }

        val vehicleFromDir2 = intersection.vehicles.firstOrNull { it.startRoad == dir2 }
        vehicleFromDir2?.let {
            leftVehicles.add(it.id)
            intersection.vehicles.remove(it)
        }

        // KROK 4: Zmieniamy światła, jeżeli nie przejechał żaden samochód, lub jeśli przekroczono dopuszczalną długość cyklu.
        if ((!dir1HasVehicles && !dir2HasVehicles) || intersection.currentCycleDuration >= cycleDuration) {
            intersection.changeLights()
            intersection.currentCycleDuration = 0
        }

        // KROK 5: Po zakończonym kroku zwiększamy długość obecnego cyklu o 1
        intersection.currentCycleDuration++

        // KROK 6: zwracamy ID pojazdów, które przejechały
        return leftVehicles
    }




    fun runCommands(commands: List<Command>): List<StepStatus> {
        val stepStatuses = mutableListOf<StepStatus>()

        for (command in commands) {
            when (command.type) {
                "addVehicle" -> addVehicle(
                    Vehicle(
                        id = command.vehicleId!!,
                        startRoad = command.startRoad!!,
                        endRoad = command.endRoad!!
                    )
                )
                "step" -> {
                    intersection.currentCycleDuration += 1
                    val leftVehicles = step()
                    stepStatuses.add(StepStatus(leftVehicles))
                }
                else -> throw IllegalArgumentException("Nieznany typ komendy: ${command.type}")
            }
        }
        return stepStatuses
    }

    fun getIntersection(): Intersection {
        return intersection
    }
}
