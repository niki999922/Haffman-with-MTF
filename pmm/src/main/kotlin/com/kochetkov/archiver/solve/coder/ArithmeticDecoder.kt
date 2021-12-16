package com.kochetkov.archiver.solve.coder

import com.kochetkov.archiver.solve.CheckedFrequencyTable
import com.kochetkov.archiver.solve.FrequencyTable
import com.kochetkov.archiver.solve.stream.CodingIS


class ArithmeticDecoder(numBits: Int, private val input: CodingIS) : ArithmeticCoderBase(numBits) {
    private var code: Long

    init {
        code = 0
        for (i in 0 until numStateBits) code = code shl 1 or readCodeBit().toLong()
    }

    fun read(frequency: FrequencyTable): Int {
        val frequencyS = CheckedFrequencyTable(frequency)
        val total = frequencyS.total.toLong()
        val range = high - low + 1
        val offset = code - low
        val value = ((offset + 1) * total - 1) / range

        var start = 0
        var end = frequencyS.symbolLimit
        while (end - start > 1) {
            val middle = start + end ushr 1
            if (frequencyS.getLow(middle) > value) end = middle else start = middle
        }
        val symbol = start
        update(frequencyS, symbol)
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