package app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.io.File

class Cli : CliktCommand() {
    private val file: File by argument().file(mustExist = true, mustBeReadable = true, mustBeWritable = true)

    private val notDeep by option(help = "Do not remove deep (iteratively until there are no more)").flag()
    private val quiet by option("-q", "--quiet", help = "No output to console").flag()

    override fun run() {
        val scavenger = Scavenger()
        if (file.isDirectory) {
            file.walk().forEach {
                overrideFile(scavenger, it)
            }
        } else {
            overrideFile(scavenger, file)
        }
    }

    private fun overrideFile(scavenger: Scavenger, file: File) {
        if (file.extension != "java") {
            if (!quiet) {
                println("Not Java file ${file.path}")
            }
            return
        }
        if (!quiet) {
            println("File ${file.path}:")
        }
        val result = scavenger.removeUnusedVarDecls(file.readText(), System.out, notDeep, quiet)
        file.writeText(result)
    }
}

fun main(args: Array<String>) = Cli().main(args)
