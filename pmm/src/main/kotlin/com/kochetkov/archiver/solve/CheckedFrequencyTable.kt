package com.kochetkov.archiver.solve

import java.lang.AssertionError

class CheckedFrequencyTable(private val freqTable: FrequencyTable) : FrequencyTable {
    override val symbolLimit: Int
        get() {
            val result = freqTable.symbolLimit
            return result
        }

    override fun get(symbol: Int): Int {
        val result = freqTable[symbol]
        return result
    }

    override val total: Int
        get() {
            return freqTable.total
        }

    override fun getLow(symbol: Int): Int {
        return if (isSymbolInRange(symbol)) {
            freqTable.getLow(symbol)
        } else {
            freqTable.getLow(symbol)
            throw AssertionError("IllegalArgumentException expected")
        }
    }

    override fun getHigh(symbol: Int): Int {
        return if (isSymbolInRange(symbol)) {
            freqTable.getHigh(symbol)
        } else {
            freqTable.getHigh(symbol)
            throw AssertionError("IllegalArgumentException expected")
        }
    }


    override fun set(symbol: Int, freq: Int) {
        freqTable[symbol] = freq
    }

    override fun increment(symbol: Int) {
        freqTable.increment(symbol)
    }

    private fun isSymbolInRange(symbol: Int): Boolean {
        return 0 <= symbol && symbol < symbolLimit
    }
}