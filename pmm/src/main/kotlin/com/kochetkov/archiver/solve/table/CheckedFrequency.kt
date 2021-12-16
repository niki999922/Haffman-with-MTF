package com.kochetkov.archiver.solve.table


class CheckedFrequency(private val freqTable: Frequency) : Frequency {
    override val limit: Int
        get() {
            return freqTable.limit
        }

    override fun get(symbol: Int): Int {
        return freqTable[symbol]
    }

    override val total: Int
        get() {
            return freqTable.total
        }

    override fun down(symbol: Int): Int = freqTable.down(symbol)

    override fun top(symbol: Int): Int  = freqTable.top(symbol)

    override fun set(symbol: Int, frequency: Int) {
        freqTable[symbol] = frequency
    }

    override fun inc(symbol: Int) {
        freqTable.inc(symbol)
    }
}