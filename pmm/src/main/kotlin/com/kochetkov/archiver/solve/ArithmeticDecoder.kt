package com.kochetkov.archiver.solve

import kotlin.Throws
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.AssertionError
import java.util.Objects

/*
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */ /**
 * Reads from an arithmetic-coded bit stream and decodes symbols. Not thread-safe.
 * @see ArithmeticEncoder
 */
class ArithmeticDecoder(numBits: Int, `in`: BitInputStream) : ArithmeticCoderBase(numBits) {
    /*---- Fields ----*/ // The underlying bit input stream (not null).
    private val input: BitInputStream

    // The current raw code bits being buffered, which is always in the range [low, high].
    private var code: Long
    /*---- Methods ----*/
    /**
     * Decodes the next symbol based on the specified frequency table and returns it.
     * Also updates this arithmetic coder's state and may read in some bits.
     * @param freqs the frequency table to use
     * @return the next symbol
     * @throws NullPointerException if the frequency table is `null`
     * @throws IOException if an I/O exception occurred
     */
    @Throws(IOException::class)
    fun read(freqs: FrequencyTable?): Int {
        return read(CheckedFrequencyTable(freqs!!))
    }

    /**
     * Decodes the next symbol based on the specified frequency table and returns it.
     * Also updates this arithmetic coder's state and may read in some bits.
     * @param freqs the frequency table to use
     * @return the next symbol
     * @throws NullPointerException if the frequency table is `null`
     * @throws IllegalArgumentException if the frequency table's total is too large
     * @throws IOException if an I/O exception occurred
     */
    @Throws(IOException::class)
    fun read(freqs: CheckedFrequencyTable): Int {
        // Translate from coding range scale to frequency table scale
        val total = freqs.total.toLong()
        require(total <= maximumTotal) { "Cannot decode symbol because total is too large" }
        val range = high - low + 1
        val offset = code - low
        val value = ((offset + 1) * total - 1) / range
        if (value * range / total > offset) throw AssertionError()
        if (value < 0 || value >= total) throw AssertionError()

        // A kind of binary search. Find highest symbol such that freqs.getLow(symbol) <= value.
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

    @Throws(IOException::class)
    override fun shift() {
        code = code shl 1 and stateMask or readCodeBit().toLong()
    }

    @Throws(IOException::class)
    override fun underflow() {
        code = code and halfRange or (code shl 1 and (stateMask ushr 1)) or readCodeBit().toLong()
    }

    // Returns the next bit (0 or 1) from the input stream. The end
    // of stream is treated as an infinite number of trailing zeros.
    @Throws(IOException::class)
    private fun readCodeBit(): Int {
        var temp = input.read()
        if (temp == -1) temp = 0
        return temp
    }
    /*---- Constructor ----*/ /**
     * Constructs an arithmetic coding decoder based on the
     * specified bit input stream, and fills the code bits.
     * @param numBits the number of bits for the arithmetic coding range
     * @param in the bit input stream to read from
     * @throws NullPointerException if the input steam is `null`
     * @throws IllegalArgumentException if stateSize is outside the range [1, 62]
     * @throws IOException if an I/O exception occurred
     */
    init {
        input = Objects.requireNonNull(`in`)
        code = 0
        for (i in 0 until numStateBits) code = code shl 1 or readCodeBit().toLong()
    }
}