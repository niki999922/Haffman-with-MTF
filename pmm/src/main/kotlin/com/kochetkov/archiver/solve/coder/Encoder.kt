package com.kochetkov.archiver.solve.coder

import com.kochetkov.archiver.solve.CheckedFrequencyTable
import com.kochetkov.archiver.solve.FrequencyTable
import com.kochetkov.archiver.solve.stream.CodingOS


class Encoder(numBits: Int, val output: CodingOS) : Core(numBits) {
    var numUnderflow: Int = 0

    fun write(frequency: FrequencyTable, symbol: Int) { update(CheckedFrequencyTable(frequency), symbol) }

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