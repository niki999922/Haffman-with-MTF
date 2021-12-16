package com.kochetkov.archiver.solve

import java.io.*

object AdaptiveArithmeticCompress {
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