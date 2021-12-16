package com.kochetkov.archiver.solve

import java.io.Closeable
import java.lang.AutoCloseable
import kotlin.Throws
import java.io.IOException
import java.io.OutputStream
import java.lang.IllegalArgumentException
import java.util.Objects

class BitOutputStream(private val output: OutputStream) : Closeable {
    private var currentByte: Int
    private var numBitsFilled: Int

    init {
        currentByte = 0
        numBitsFilled = 0
    }

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