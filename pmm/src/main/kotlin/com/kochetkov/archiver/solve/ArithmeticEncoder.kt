package com.kochetkov.archiver.solve


class ArithmeticEncoder(numBits: Int, val output: BitOutputStream) : ArithmeticCoderBase(numBits) {
    private var numUnderflow: Int = 0

    fun write(freqs: FrequencyTable, symbol: Int) {
        write(CheckedFrequencyTable(freqs), symbol)
    }

    fun write(freqs: CheckedFrequencyTable, symbol: Int) {
        update(freqs, symbol)
    }

    override fun shift() {
        val bit = (low ushr numStateBits - 1).toInt()
        output.write(bit)

        while (numUnderflow > 0) {
            output.write(bit xor 1)
            numUnderflow--
        }
    }

    override fun underflow() {
        numUnderflow++
    }
}