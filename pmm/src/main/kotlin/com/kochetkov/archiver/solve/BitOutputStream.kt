package com.kochetkov.archiver.solve

import java.io.Closeable
import java.lang.AutoCloseable
import kotlin.Throws
import java.io.IOException
import java.io.OutputStream
import java.lang.IllegalArgumentException
import java.util.Objects

class BitOutputStream(out: OutputStream) : Closeable {
    private val output: OutputStream
    private var currentByte: Int
    private var numBitsFilled: Int

    init {
        output = Objects.requireNonNull(out)
        currentByte = 0
        numBitsFilled = 0
    }

    fun write(b: Int) {
        require(!(b != 0 && b != 1)) { "Argument must be 0 or 1" }
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