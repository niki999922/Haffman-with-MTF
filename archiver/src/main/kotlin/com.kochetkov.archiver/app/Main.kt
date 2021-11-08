package com.kochetkov.archiver.app

import com.kochetkov.archiver.Solve
import java.io.File

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size < 3) {
                System.err.println("Expected tree arguments: <decode|encode> <input> <output>")
                return
            }

            val mode = args[0]
            val inputFile = File(args[1])
            val outputFile = File(args[2])

            if (mode != "encode" && mode != "decode") {
                System.err.println("Invalid mode used: <decode|encode> <input> <output>")
                return
            }

            if (!inputFile.exists()) {
                System.err.println("Input file doesn't exist")
                return
            }

            Solve(mode,inputFile,outputFile).solve()
        }
    }
}