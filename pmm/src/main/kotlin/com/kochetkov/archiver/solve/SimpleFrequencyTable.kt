package com.kochetkov.archiver.solve


class SimpleFrequencyTable : FrequencyTable {
    private var frequencies: IntArray
    private var cumulative: IntArray?
    override var total: Int


    constructor(freqs: FrequencyTable) {
        val numSym = freqs.symbolLimit
        frequencies = IntArray(numSym)
        total = 0
        for (i in frequencies.indices) {
            val x = freqs[i]
            frequencies[i] = x
            total = checkedAdd(x, total)
        }
        cumulative = null
    }

    override val symbolLimit: Int
        get() = frequencies.size

    override fun get(symbol: Int): Int {
        return frequencies[symbol]
    }

    override fun set(symbol: Int, freq: Int) {
        val temp = total - frequencies[symbol]
        total = checkedAdd(temp, freq)
        frequencies[symbol] = freq
        cumulative = null
    }

    override fun increment(symbol: Int) {
        total = checkedAdd(total, 1)
        frequencies[symbol]++
        cumulative = null
    }


    override fun getLow(symbol: Int): Int {
        if (cumulative == null) initCumulative()
        return cumulative!![symbol]
    }

    override fun getHigh(symbol: Int): Int {
        if (cumulative == null) initCumulative()
        return cumulative!![symbol + 1]
    }

    private fun initCumulative() {
        cumulative = IntArray(frequencies.size + 1)
        var sum = 0
        for (i in frequencies.indices) {
            sum = checkedAdd(frequencies[i], sum)
            cumulative!![i + 1] = sum
        }
    }


    companion object {
        private fun checkedAdd(x: Int, y: Int): Int {
            return x + y
        }
    }
}