package com.kochetkov.archiver.app

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.ParseException
import java.io.File

internal class ApplicationArgumentsHandler {
    companion object {
        fun handle(args: Array<String>): ApplicationRunningParameters? {
            val commandLine = parseArguments(args) ?: return null

            if (commandLine.hasOption("help")) {
                CLIProducer.printHelp()
                return null
            }

            if (!commandLine.validateArguments()) return null

            val applicationRunningParameters = ApplicationRunningParameters(commandLine)

            return applicationRunningParameters
        }


        private fun parseArguments(args: Array<String>): CommandLine? {
            var commandLine: CommandLine? = null

            try {
                commandLine = CLIProducer.parser.parse(CLIProducer.options, args)
            } catch (exception: ParseException) {
                println(exception.message)
                CLIProducer.printHelp()
            }

            return commandLine
        }

        private fun CommandLine.validateArguments(): Boolean {
            if (hasOption("s")) {
                val sarifFile = getOptionValue("s")
                if (!File(sarifFile).exists()) {
                    println("file \"$sarifFile\" does not exist")
                    return false
                }
            } else {
                println("\"-s\" parameters have to be specified, see --help")
                return false
            }

            return true
        }
    }
}