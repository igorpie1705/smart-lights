package main

import service.SimulationService
import util.FileHandler
import util.JsonParser

fun main() {
    // Czytamy i zapisujemy zawartość pliku wejściowego.
    val jsonString = FileHandler.readFile("src/main/resources/input.json")

    // Parsujemy komendy z JSON do zmiennej typu string.
    val commands = JsonParser.parseCommands(jsonString)

    // Tworzymy instancję symulacji.
    val simulation = SimulationService()

    // Wykonujemy komendy i otrzymujemy statusy kroków.
    val stepStatuses = simulation.runCommands(commands.commands)

    // Wypisujemy w konsoli status każdego kroku.
    stepStatuses.forEachIndexed { index, status ->
        println("Krok $index: Pojazdy, które opuściły skrzyżowanie: ${status.leftVehicles}")
    }

    // Parsujemy statusy kroków ze zmiennej typu string, na JSON.
    val outputJson = JsonParser.toJson(stepStatuses)

    // Zapisujemy poprzednio zapisany JSON do pliku output.json.
    FileHandler.writeFile("src/main/resources/output.json", outputJson)


}