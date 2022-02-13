package app

import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>) {
    println(Paths.get(".").toAbsolutePath().normalize().toString())
    args.forEach { println(Inspector().inspect(File(it))) }
    Inspector().inspect(File("src/test/resources/test.java"))
}
