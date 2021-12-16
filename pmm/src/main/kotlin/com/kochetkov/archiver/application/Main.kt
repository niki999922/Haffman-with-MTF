package com.kochetkov.archiver.application

import com.kochetkov.archiver.Solve
import com.kochetkov.archiver.solve.*
import com.kochetkov.archiver.solve.stream.CodingIS
import com.kochetkov.archiver.solve.stream.CodingOS
import com.kochetkov.archiver.solve.util.compress
import com.kochetkov.archiver.solve.util.decompress
import java.io.*
import java.nio.file.Files

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val doEncode = true
//            val doEncode = false

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

                    BufferedInputStream(FileInputStream(tempFile)).use { input ->
                        CodingOS(BufferedOutputStream(FileOutputStream(outputFile))).use { out ->
                            compress(input, out)
                        }
                    }
                } else {
                    CodingIS(BufferedInputStream(FileInputStream(inputFile))).use { input ->
                        BufferedOutputStream(FileOutputStream(tempFile)).use { out ->
                            decompress(input, out)
                        }
                    }

                    Solve("decode", tempFile, outputFile).solve()
                }
            } finally {
                println("TEEEEEEMP FILE: ${tempFile.toPath().toAbsolutePath()}")
//                tempFile.delete()
            }
        }
    }
}