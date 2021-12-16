package com.kochetkov.archiver.solve

import java.io.*

object AdaptiveArithmeticDecompress {
    fun decompress(`in`: BitInputStream?, out: OutputStream) {
        val initFreqs = FlatFrequencyTable(257)
        val freqs: FrequencyTable = SimpleFrequencyTable(initFreqs)
        val dec = ArithmeticDecoder(32, `in`!!)
        while (true) {
            // Decode and write one byte
            val symbol: Int = dec.read(freqs)
            if (symbol == 256) // EOF symbol
                break
            out.write(symbol)
            freqs.increment(symbol)
        }
    }
}