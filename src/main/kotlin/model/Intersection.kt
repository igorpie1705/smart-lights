package model

import main.model.TrafficLightState

data class Intersection(
    val trafficLights: List<TrafficLight>,
    val vehicles: MutableList<Vehicle>,
    var currentCycleDuration: Int,
) {
    fun getActiveDirectionPair(): Pair<String, String>? {
        val greenLights = trafficLights.filter { it.state == TrafficLightState.GREEN }
        return when {
            greenLights.any { it.direction == "north" } && greenLights.any { it.direction == "south" } ->
                Pair("north", "south")
            greenLights.any { it.direction == "east" } && greenLights.any { it.direction == "west" } ->
                Pair("east", "west")
            else -> null
        }
    }

    fun changeLights() {
        val currentPair = getActiveDirectionPair()
        val nextPair = when (currentPair?.first) {
            "north" -> Pair("east", "west")
            "east" -> Pair("north", "south")
            else -> Pair("north", "south")
        }

        // Wyłącz wszystkie światła
        trafficLights.forEach { it.state = TrafficLightState.RED }
        // Włącz nową parę
        trafficLights.find { it.direction == nextPair.first }?.state = TrafficLightState.GREEN
        trafficLights.find { it.direction == nextPair.second }?.state = TrafficLightState.GREEN
    }

}