package com.kochetkov.archiver.solve

import java.lang.UnsupportedOperationException

class FlatFrequencyTable(numSyms: Int) : FrequencyTable {
    override val total: Int

    init {
        require(numSyms >= 1) { "Number of symbols must be positive" }
        total = numSyms
    }

    override val symbolLimit: Int
        get() = total

    override fun get(symbol: Int): Int {
        checkSymbol(symbol)
        return 1
    }

    override fun getLow(symbol: Int): Int {
        checkSymbol(symbol)
        return symbol
    }

    override fun getHigh(symbol: Int): Int {
        checkSymbol(symbol)
        return symbol + 1
    }

    private fun checkSymbol(symbol: Int) {
        require(!(symbol < 0 || symbol >= total)) { "Symbol out of range" }
    }

    override fun toString(): String {
        return "FlatFrequencyTable=" + total
    }

    override fun set(symbol: Int, freq: Int) {
        throw UnsupportedOperationException()
    }

    override fun increment(symbol: Int) {
        throw UnsupportedOperationException()
    }
}