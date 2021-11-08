package com.kochetkov.archiver.app

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val applicationRunningParameters = ApplicationArgumentsHandler.handle(args) ?: return
            println("Starting exporting sarif")

            runCatching {
                applicationRunningParameters.run {
//                    something
                }
                println("Done")
            }.onFailure { ex ->
                println(ex)
            }
        }
    }
}