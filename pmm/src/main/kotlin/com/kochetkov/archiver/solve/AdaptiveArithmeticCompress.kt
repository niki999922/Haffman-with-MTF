package com.kochetkov.archiver.solve

import java.io.*

/**
 * Compression application using adaptive arithmetic coding.
 *
 * Usage: java AdaptiveArithmeticCompress InputFile OutputFile
 *
 * Then use the corresponding "AdaptiveArithmeticDecompress" application to recreate the original input file.
 *
 * Note that the application starts with a flat frequency table of 257 symbols (all set to a frequency of 1),
 * and updates it after each byte encoded. The corresponding decompressor program also starts with a flat
 * frequency table and updates it after each byte decoded. It is by design that the compressor and
 * decompressor have synchronized states, so that the data can be decompressed properly.
 */
object AdaptiveArithmeticCompress {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        // Handle command line arguments
//        if (args.size != 2) {
//            System.err.println("Usage: java AdaptiveArithmeticCompress InputFile OutputFile")
//            System.exit(1)
//            return
//        }
//        val inputFile = File(args[0])
//        val outputFile = File(args[1])
//
//
//        BufferedInputStream(FileInputStream(inputFile)).use { input ->
//            BitOutputStream(BufferedOutputStream(FileOutputStream(outputFile))).use { out ->
//                compress(input, out)
//            }
//        }
    }

    // To allow unit testing, this method is package-private instead of private.
    @Throws(IOException::class)
    fun compress(input: InputStream, out: BitOutputStream?) {
        val initFreqs = FlatFrequencyTable(257)
        val freqs: FrequencyTable = SimpleFrequencyTable(initFreqs)
        val enc = ArithmeticEncoder(32, out!!)
        while (true) {
            // Read and encode one byte
            val symbol = input.read()
            if (symbol == -1) break
            enc.write(freqs, symbol)
            freqs.increment(symbol)
        }
        enc.write(freqs, 256) // EOF
        enc.finish() // Flush remaining code bits
    }
}