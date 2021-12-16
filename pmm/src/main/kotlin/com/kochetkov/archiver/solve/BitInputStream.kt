package com.kochetkov.archiver.solve

import java.io.Closeable
import kotlin.Throws
import java.io.IOException
import java.lang.AssertionError
import java.io.EOFException
import java.io.InputStream
import java.util.Objects

class BitInputStream(private val input: InputStream) : Closeable {
    private var currentByte: Int
    private var numBitsRemaining: Int

    init {
        currentByte = 0
        numBitsRemaining = 0
    }

    fun read(): Int {
        if (currentByte == -1) return -1
        if (numBitsRemaining == 0) {
            currentByte = input.read()
            if (currentByte == -1) return -1
            numBitsRemaining = 8
        }
        numBitsRemaining--
        return currentByte ushr numBitsRemaining and 1
    }

    override fun close() {
        input.close()
        currentByte = -1
        numBitsRemaining = 0
    }
}