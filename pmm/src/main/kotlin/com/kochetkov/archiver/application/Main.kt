package com.kochetkov.archiver.application

import com.kochetkov.archiver.Solve
import com.kochetkov.archiver.solve.AdaptiveArithmeticCompress
import com.kochetkov.archiver.solve.AdaptiveArithmeticDecompress
import java.io.File
import java.nio.file.Files

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val doEncode = false

            if (args.size < 2) {
                System.err.println("Expected tree arguments: <input> <output>")
                return
            }

            val inputFile = File(args[0])
            val outputFile = File(args[1])
            val tempFile = Files.createTempFile("temp_dec_enc", "txt").toFile()

            if (!inputFile.exists()) {
                System.err.println("Input file doesn't exist")
                return
            }

            try {
                if (doEncode) {
                    Solve("encode", inputFile, tempFile).solve()
                    AdaptiveArithmeticCompress.main(arrayOf(tempFile.toPath().toAbsolutePath().toString(), outputFile.toPath().toAbsolutePath().toString()))
                } else {
                    AdaptiveArithmeticDecompress.main(arrayOf(inputFile.toPath().toAbsolutePath().toString(), tempFile.toPath().toAbsolutePath().toString()))
                    Solve("decode", tempFile, outputFile).solve()
                }
            } finally {
                println("TEEEEEEMP FILE: ${tempFile.toPath().toAbsolutePath()}")
//                tempFile.delete()
            }
        }
    }
}