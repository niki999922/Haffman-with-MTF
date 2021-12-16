package com.kochetkov.archiver.solve

import com.kochetkov.archiver.solve.coder.Decoder
import com.kochetkov.archiver.solve.coder.Encoder
import com.kochetkov.archiver.solve.stream.CodingIS
import com.kochetkov.archiver.solve.stream.CodingOS
import com.kochetkov.archiver.solve.table.SimpleFrequency
import java.io.InputStream
import java.io.OutputStream


fun compress(input: InputStream, out: CodingOS) {
    val frequency = SimpleFrequency()
    val enc = Encoder(32, out)
    while (true) {
        val symbol = input.read()
        if (symbol == -1) break
        enc.write(frequency, symbol)
        frequency.increment(symbol)
    }
    enc.write(frequency, 256)
    enc.output.write(1)
}

fun decompress(input: CodingIS, out: OutputStream) {
    val frequency = SimpleFrequency()
    val dec = Decoder(32, input)
    while (true) {
        val symbol: Int = dec.read(frequency)
        if (symbol == 256) break
        out.write(symbol)
        frequency.increment(symbol)
    }
}

fun CodingOS.addTo8th() {
    while (bitsWritten != 0) write(0)
}

fun CodingOS.flushBuff() {
    if (bitsWritten == 8) {
        output.write(byte)
        byte = 0
        bitsWritten = 0
    }
}