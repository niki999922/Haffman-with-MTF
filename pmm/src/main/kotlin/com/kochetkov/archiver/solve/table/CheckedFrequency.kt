package com.kochetkov.archiver.solve.table


class CheckedFrequency(private val freqTable: Frequency) : Frequency {
    override val symbolLimit: Int
        get() {
            return freqTable.symbolLimit
        }

    override fun get(symbol: Int): Int {
        return freqTable[symbol]
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