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
        val inspector = Inspector()
        if (file.isDirectory) {
            file.walk().forEach {
                inspector.removeUnusedVarDeclsFromFile(it, notDeep, quiet)
            }
        } else {
            inspector.removeUnusedVarDeclsFromFile(file, notDeep, quiet)
        }
    }
}

fun main(args: Array<String>) = Cli().main(args)
