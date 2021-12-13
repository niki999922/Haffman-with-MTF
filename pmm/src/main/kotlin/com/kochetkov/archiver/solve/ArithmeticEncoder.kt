package com.kochetkov.archiver.solve

import java.lang.ArithmeticException
import java.util.Objects

class ArithmeticEncoder(numBits: Int, out: BitOutputStream) : ArithmeticCoderBase(numBits) {
    private val output: BitOutputStream
    private var numUnderflow: Int

    init {
        output = Objects.requireNonNull(out)
        numUnderflow = 0
    }

    fun write(freqs: FrequencyTable?, symbol: Int) {
        write(CheckedFrequencyTable(freqs!!), symbol)
    }

    fun write(freqs: CheckedFrequencyTable?, symbol: Int) {
        update(freqs!!, symbol)
    }

    fun finish() {
        output.write(1)
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
        if (numUnderflow == Int.MAX_VALUE) throw ArithmeticException("Maximum underflow reached")
        numUnderflow++
    }
}