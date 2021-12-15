package com.kochetkov.archiver.solve

import java.io.*
import kotlin.Throws
import kotlin.jvm.JvmStatic
import java.lang.IllegalArgumentException

/**
 * Compression application using static arithmetic coding.
 *
 * Usage: java ArithmeticCompress InputFile OutputFile
 *
 * Then use the corresponding "ArithmeticDecompress" application to recreate the original input file.
 *
 * Note that the application uses an alphabet of 257 symbols - 256 symbols for the byte
 * values and 1 symbol for the EOF marker. The compressed file format starts with a list
 * of 256 symbol frequencies, and then followed by the arithmetic-coded data.
 */
object ArithmeticCompress {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        // Handle command line arguments
        if (args.size != 2) {
            System.err.println("Usage: java ArithmeticCompress InputFile OutputFile")
            System.exit(1)
            return
        }
        val inputFile = File(args[0])
        val outputFile = File(args[1])

        // Read input file once to compute symbol frequencies
        val freqs: FrequencyTable = getFrequencies(inputFile)
        freqs.increment(256) // EOF symbol gets a frequency of 1
        BufferedInputStream(FileInputStream(inputFile)).use { `in` ->
            BitOutputStream(BufferedOutputStream(FileOutputStream(outputFile))).use { out ->
                writeFrequencies(out, freqs)
                compress(freqs, `in`, out)
            }
        }
    }

    // Returns a frequency table based on the bytes in the given file.
    // Also contains an extra entry for symbol 256, whose frequency is set to 0.
    @Throws(IOException::class)
    private fun getFrequencies(file: File): FrequencyTable {
        val freqs: FrequencyTable = SimpleFrequencyTable(IntArray(257))
        BufferedInputStream(FileInputStream(file)).use { input ->
            while (true) {
                val b = input.read()
                if (b == -1) break
                freqs.increment(b)
            }
        }
        return freqs
    }

    // To allow unit testing, this method is package-private instead of private.
    @Throws(IOException::class)
    fun writeFrequencies(out: BitOutputStream, freqs: FrequencyTable) {
        for (i in 0..255) writeInt(out, 32, freqs.get(i))
    }

    // To allow unit testing, this method is package-private instead of private.
    @Throws(IOException::class)
    fun compress(freqs: FrequencyTable?, `in`: InputStream, out: BitOutputStream?) {
        val enc = ArithmeticEncoder(32, out!!)
        while (true) {
            val symbol = `in`.read()
            if (symbol == -1) break
            enc.write(freqs, symbol)
        }
        enc.write(freqs, 256) // EOF
        enc.finish() // Flush remaining code bits
    }

    // Writes an unsigned integer of the given bit width to the given stream.
    @Throws(IOException::class)
    private fun writeInt(out: BitOutputStream, numBits: Int, value: Int) {
        require(!(numBits < 0 || numBits > 32))
        for (i in numBits - 1 downTo 0) out.write(value ushr i and 1) // Big endian
    }
}