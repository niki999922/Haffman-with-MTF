package com.kochetkov.archiver.solve

import java.lang.AssertionError
import java.util.Objects

class ArithmeticDecoder(numBits: Int, input: BitInputStream) : ArithmeticCoderBase(numBits) {
    private val input: BitInputStream
    private var code: Long

    init {
        this.input = Objects.requireNonNull(input)
        code = 0
        for (i in 0 until numStateBits) code = code shl 1 or readCodeBit().toLong()
    }

    fun read(freqs: FrequencyTable?): Int {
        return read(CheckedFrequencyTable(freqs!!))
    }

    fun read(freqs: CheckedFrequencyTable): Int {
        val total = freqs.total.toLong()
        require(total <= maximumTotal) { "Cannot decode symbol because total is too large" }
        val range = high - low + 1
        val offset = code - low
        val value = ((offset + 1) * total - 1) / range
        if (value * range / total > offset) throw AssertionError()
        if (value < 0 || value >= total) throw AssertionError()

        var start = 0
        var end = freqs.symbolLimit
        while (end - start > 1) {
            val middle = start + end ushr 1
            if (freqs.getLow(middle) > value) end = middle else start = middle
        }
        if (start + 1 != end) throw AssertionError()
        val symbol = start
        if (offset < freqs.getLow(symbol) * range / total || freqs.getHigh(symbol) * range / total <= offset) throw AssertionError()
        update(freqs, symbol)
        if (code < low || code > high) throw AssertionError("Code out of range")
        return symbol
    }

    override fun shift() {
        code = code shl 1 and stateMask or readCodeBit().toLong()
    }

    override fun underflow() {
        code = code and halfRange or (code shl 1 and (stateMask ushr 1)) or readCodeBit().toLong()
    }

    private fun readCodeBit(): Int {
        var temp = input.read()
        if (temp == -1) temp = 0
        return temp
    }
}