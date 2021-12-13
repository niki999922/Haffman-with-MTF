package com.kochetkov.archiver.app

import java.io.File

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size < 2) {
                System.err.println("Expected tree arguments: <input> <output>")
                return
            }

            val inputFile = File(args[0])
            val outputFile = File(args[1])

            if (!inputFile.exists()) {
                System.err.println("Input file doesn't exist")
                return
            }

//            Solve(args[2], inputFile, outputFile).solve()
//            Solve("encode", inputFile, outputFile).solve()
//            Solve("decode", inputFile, outputFile).solve()
        }
    }
}