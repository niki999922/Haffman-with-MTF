package com.kochetkov.archiver.app

import org.apache.commons.cli.CommandLine
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

internal class ApplicationRunningParameters(commandLine: CommandLine) {
    val output: Path = if (commandLine.hasOption("o")) {
        Paths.get(commandLine.getOptionValue("o"))
    } else {
        Paths.get(".")
    }
}
