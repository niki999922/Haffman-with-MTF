package com.kochetkov.archiver.solve.table

class FlatFrequency(override val total: Int) : Frequency {
    override val limit: Int
        get() = total

    override fun get(symbol: Int) = 1
    override fun down(symbol: Int) = symbol
    override fun top(symbol: Int) = symbol + 1

    override fun set(symbol: Int, frequency: Int) {
        //TODO: not need
    }
    override fun inc(symbol: Int) {
        //TODO: not need
    }
}