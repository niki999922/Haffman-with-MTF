package com.kochetkov.archiver.solve


class SimpleFrequencyTable(frequency: FrequencyTable) : FrequencyTable {
    private var frequencies: IntArray
    private var cumulative: IntArray?
    override var total: Int = 0


    init {
        val numSym = frequency.symbolLimit
        frequencies = IntArray(numSym)
        total = 0
        for (i in frequencies.indices) {
            val x = frequency[i]
            frequencies[i] = x
            total += x
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
        total = temp + freq
        frequencies[symbol] = freq
        cumulative = null
    }

    override fun increment(symbol: Int) {
        total += 1
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
            sum += frequencies[i]
            cumulative!![i + 1] = sum
        }
    }
}