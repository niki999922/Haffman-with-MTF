package com.kochetkov.archiver.solve

import java.io.Closeable
import java.io.OutputStream

class BitOutputStream(private val output: OutputStream) : Closeable {
    private var currentByte = 0
    private var numBitsFilled = 0

    fun write(b: Int) {
        currentByte = currentByte shl 1 or b
        numBitsFilled++
        if (numBitsFilled == 8) {
            output.write(currentByte)
            currentByte = 0
            numBitsFilled = 0
        }
    }

    override fun close() {
        while (numBitsFilled != 0) write(0)
        output.close()
    }
}