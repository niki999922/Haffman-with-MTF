package com.kochetkov.archiver.solve

import java.io.InputStream
import java.io.OutputStream


fun compress(input: InputStream, out: BitOutputStream) {
    val initFreqs = FlatFrequencyTable(257)
    val freqs: FrequencyTable = SimpleFrequencyTable(initFreqs)
    val enc = ArithmeticEncoder(32, out)
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

fun decompress(input: BitInputStream, out: OutputStream) {
    val initFreqs = FlatFrequencyTable(257)
    val freqs: FrequencyTable = SimpleFrequencyTable(initFreqs)
    val dec = ArithmeticDecoder(32, input)
    while (true) {
        // Decode and write one byte
        val symbol: Int = dec.read(freqs)
        if (symbol == 256) // EOF symbol
            break
        out.write(symbol)
        freqs.increment(symbol)
    }
}

