package com.kochetkov.archiver.solve.stream

import java.io.Closeable
import java.io.InputStream

class CodingIS(val input: InputStream) : Closeable {
    var byte = 0
    var bitsOst = 0

    fun read(): Int {
        if (byte == -1) return -1
        if (bitsOst == 0) {
            byte = input.read()
            if (byte == -1) return -1
            bitsOst = 8
        }
        bitsOst--
        return byte ushr bitsOst and 1
    }

    override fun close() {
        input.close()
        bitsOst = 0
        byte = -1
    }
}