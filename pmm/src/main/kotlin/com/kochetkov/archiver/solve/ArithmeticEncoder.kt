package com.kochetkov.archiver.solve


class ArithmeticEncoder(numBits: Int, val output: BitOutputStream) : ArithmeticCoderBase(numBits) {
    private var numUnderflow: Int = 0

    fun write(frequency: FrequencyTable, symbol: Int) {
        update(CheckedFrequencyTable(frequency), symbol)
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