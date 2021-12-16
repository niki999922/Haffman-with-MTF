package com.kochetkov.archiver.solve

import java.io.Closeable
import kotlin.Throws
import java.io.IOException
import java.lang.AssertionError
import java.io.EOFException
import java.io.InputStream
import java.util.Objects

class BitInputStream(input: InputStream) : Closeable {
    private val input: InputStream
    private var currentByte: Int
    private var numBitsRemaining: Int

    init {
        this.input = Objects.requireNonNull(input)
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
        if (numBitsRemaining <= 0) throw AssertionError()
        numBitsRemaining--
        return currentByte ushr numBitsRemaining and 1
    }

    override fun close() {
        input.close()
        currentByte = -1
        numBitsRemaining = 0
    }
}