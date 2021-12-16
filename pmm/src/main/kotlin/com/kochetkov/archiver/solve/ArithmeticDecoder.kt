package com.kochetkov.archiver.solve


class ArithmeticDecoder(numBits: Int, private val input: BitInputStream) : ArithmeticCoderBase(numBits) {
    private var code: Long

    init {
        code = 0
        for (i in 0 until numStateBits) code = code shl 1 or readCodeBit().toLong()
    }

    fun read(freqs: FrequencyTable?): Int {
        return read(CheckedFrequencyTable(freqs!!))
    }

    fun read(freqs: CheckedFrequencyTable): Int {
        val total = freqs.total.toLong()
        val range = high - low + 1
        val offset = code - low
        val value = ((offset + 1) * total - 1) / range

        var start = 0
        var end = freqs.symbolLimit
        while (end - start > 1) {
            val middle = start + end ushr 1
            if (freqs.getLow(middle) > value) end = middle else start = middle
        }
        val symbol = start
        update(freqs, symbol)
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