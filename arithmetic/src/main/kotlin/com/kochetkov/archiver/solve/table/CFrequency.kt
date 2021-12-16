package com.kochetkov.archiver.solve.table


class CFrequency(val frequency: Frequency) : Frequency {
    override fun get(symbol: Int) = frequency[symbol]

    override val limit: Int
        get() = frequency.limit

    override val total: Int
        get() = frequency.total

    override fun down(symbol: Int) = frequency.down(symbol)
    override fun top(symbol: Int) = frequency.top(symbol)

    override fun set(symbol: Int, frequency: Int) {
        this.frequency[symbol] = frequency
    }

    override fun inc(symbol: Int) {
        frequency.inc(symbol)
    }
}