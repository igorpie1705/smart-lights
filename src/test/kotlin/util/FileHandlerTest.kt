package util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class FileHandlerTest {

 @Test
 fun `test readFile reads content correctly`() {
  // Przygotowanie: tworzymy plik z przykładową zawartością
  val testFilePath = "testFile.txt"
  val expectedContent = "Hello, World!"
  File(testFilePath).writeText(expectedContent)

  // Wykonanie: odczytujemy zawartość pliku
  val actualContent = FileHandler.readFile(testFilePath)

  // Weryfikacja: sprawdzamy, czy odczytana zawartość jest zgodna z oczekiwaną
  assertEquals(expectedContent, actualContent)

  // Sprzątanie: usuwamy plik testowy
  File(testFilePath).delete()
 }

 @Test
 fun `test writeFile writes content correctly`() {
  // Przygotowanie: definiujemy ścieżkę pliku i zawartość do zapisania
  val testFilePath = "testFile.txt"
  val contentToWrite = "Kotlin is fun!"

  // Wykonanie: zapisujemy zawartość do pliku
  FileHandler.writeFile(testFilePath, contentToWrite)

  // Weryfikacja: odczytujemy plik i sprawdzamy, czy zawartość jest poprawna
  val actualContent = File(testFilePath).readText()
  assertEquals(contentToWrite, actualContent)

  // Sprzątanie: usuwamy plik testowy
  File(testFilePath).delete()
 }
}