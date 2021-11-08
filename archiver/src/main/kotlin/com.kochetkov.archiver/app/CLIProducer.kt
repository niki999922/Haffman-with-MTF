package com.kochetkov.archiver.app

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

internal object CLIProducer {
    val options = createOptions()
    val parser = DefaultParser()
    private val helper = HelpFormatter()

    fun printHelp() {
        helper.printHelp(
            100,
            "java -jar jarName",
            "Standard commands:",
            options,
            "Please report issues at link!",
            true
        )
    }

    private fun createOptions(): Options {
        val output = Option("o", "output", true, "Output directory for report files")

        return Options().addOption(output)
    }
}