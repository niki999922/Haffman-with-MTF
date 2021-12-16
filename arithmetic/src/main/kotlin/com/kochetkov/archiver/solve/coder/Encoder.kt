package com.kochetkov.archiver.solve.coder

import com.kochetkov.archiver.solve.table.CFrequency
import com.kochetkov.archiver.solve.table.Frequency
import com.kochetkov.archiver.solve.stream.CodingOS


class Encoder(numBits: Int, val output: CodingOS) : Core(numBits) {
    var numUnderflow: Int = 0

    fun write(frequency: Frequency, symbol: Int) { update(CFrequency(frequency), symbol) }

    override fun shift() {
        val bit = (low ushr stateBits - 1).toInt()
        output.write(bit)

        while (numUnderflow > 0) {
            output.write(bit xor 1)
            numUnderflow--
        }
    }

    override fun under() {
        numUnderflow++
    }
}