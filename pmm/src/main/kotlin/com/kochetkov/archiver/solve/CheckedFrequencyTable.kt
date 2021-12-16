package com.kochetkov.archiver.solve


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

    override fun getLow(symbol: Int): Int = freqTable.getLow(symbol)

    override fun getHigh(symbol: Int): Int  = freqTable.getHigh(symbol)

    override fun set(symbol: Int, freq: Int) {
        freqTable[symbol] = freq
    }

    override fun increment(symbol: Int) {
        freqTable.increment(symbol)
    }
}