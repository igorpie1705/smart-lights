package main

import service.SimulationService
import util.FileHandler
import util.JsonParser

fun main(args: Array<String>) {
    val jsonString = FileHandler.readFile("src/main/resources/input.json")

    val commands = JsonParser.parseCommands(jsonString)

    val simulation = SimulationService()

    val stepStatuses = simulation.runCommands(commands.commands)

    stepStatuses.forEachIndexed { index, status ->
        println("Krok $index: Pojazdy, które opuściły skrzyżowanie: ${status.leftVehicles}")
    }

    val outputJson = JsonParser.toJson(stepStatuses)

    FileHandler.writeFile("src/main/resources/output.json", outputJson)


}