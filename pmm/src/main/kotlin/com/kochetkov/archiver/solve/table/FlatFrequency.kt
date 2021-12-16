package com.kochetkov.archiver.solve.table

class FlatFrequency(override val total: Int) : Frequency {
    override val symbolLimit: Int
        get() = total

    override fun get(symbol: Int) = 1
    override fun getLow(symbol: Int) = symbol
    override fun getHigh(symbol: Int) = symbol + 1

    override fun set(symbol: Int, freq: Int) {
        throw UnsupportedOperationException()
    }

    override fun increment(symbol: Int) {
        throw UnsupportedOperationException()
    }
}