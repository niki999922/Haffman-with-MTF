package com.kochetkov.archiver.solve

import com.kochetkov.archiver.solve.coder.Decoder
import com.kochetkov.archiver.solve.coder.Encoder
import com.kochetkov.archiver.solve.stream.CodingIS
import com.kochetkov.archiver.solve.stream.CodingOS
import com.kochetkov.archiver.solve.table.SFrequency
import java.io.InputStream
import java.io.OutputStream


fun compress(input: InputStream, out: CodingOS) {
    val frequency = SFrequency()
    val enc = Encoder(32, out)
    while (true) {
        val symbol = input.read()
        if (symbol == -1) break
        enc.write(frequency, symbol)
        frequency.inc(symbol)
    }
    enc.write(frequency, 256)
    enc.output.write(1)
}

fun decompress(input: CodingIS, out: OutputStream) {
    val frequency = SFrequency()
    val dec = Decoder(32, input)
    while (true) {
        val symbol: Int = dec.read(frequency)
        if (symbol == 256) break
        out.write(symbol)
        frequency.inc(symbol)
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