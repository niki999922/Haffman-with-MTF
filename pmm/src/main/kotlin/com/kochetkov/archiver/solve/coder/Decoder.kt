package com.kochetkov.archiver.solve.coder

import com.kochetkov.archiver.solve.table.CFrequency
import com.kochetkov.archiver.solve.table.Frequency
import com.kochetkov.archiver.solve.stream.CodingIS


class Decoder(numBits: Int, val input: CodingIS) : Core(numBits) {
    var code: Long = 0

    init { for (i in 0 until stateBits) code = code shl 1 or readCodeBit().toLong() }

    fun read(frequency: Frequency): Int {
        val frequencyS = CFrequency(frequency)
        val total = frequencyS.total.toLong()
        val range = high - low + 1
        val offset = code - low
        val value = ((offset + 1) * total - 1) / range

        var start = 0
        var end = frequencyS.limit
        while (end - start > 1) {
            val middle = start + end ushr 1
            if (frequencyS.down(middle) > value) end = middle else start = middle
        }
        val symbol = start
        update(frequencyS, symbol)
        return symbol
    }

    override fun shift() {
        code = code shl 1 and mask or readCodeBit().toLong()
    }

    override fun under() {
        code = code and hRange or (code shl 1 and (mask ushr 1)) or readCodeBit().toLong()
    }

    private fun readCodeBit(): Int {
        var temp = input.read()
        if (temp == -1) temp = 0
        return temp
    }
}