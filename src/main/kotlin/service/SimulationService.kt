package service

import main.model.TrafficLightState
import model.*

class SimulationService {
    private val intersection = Intersection(
        trafficLights = listOf(
            TrafficLight("north", TrafficLightState.RED),
            TrafficLight("south", TrafficLightState.RED),
            TrafficLight("east", TrafficLightState.GREEN),
            TrafficLight("west", TrafficLightState.RED)
        ),
        vehicles = mutableListOf()
    )

    fun addVehicle(vehicle: Vehicle) {
        intersection.vehicles.add(vehicle)
    }

    fun step(): List<String> {
        val leftVehicles = mutableListOf<String>()
        // Algorithm
        return leftVehicles
    }

    fun runCommands(commands: List<Command>): List<StepStatus> {
        val stepStatuses = mutableListOf<StepStatus>()

        for (command in commands) {
            when (command.type) {
                "addVehicle" -> {
                    val vehicle = Vehicle(
                        id = command.vehicleId!!,
                        startRoad = command.startRoad!!,
                        endRoad = command.endRoad!!,
                    )
                    addVehicle(vehicle)
                }
                "step" -> {
                    val leftVehicles = step()
                    stepStatuses.add(StepStatus(leftVehicles))
                }
                else -> throw IllegalArgumentException("Nieznany typ komendy: ${command.type}")
            }
        }
        return stepStatuses
    }
}



