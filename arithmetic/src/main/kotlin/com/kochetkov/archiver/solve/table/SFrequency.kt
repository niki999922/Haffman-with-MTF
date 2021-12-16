package com.kochetkov.archiver.solve.table


class SFrequency : Frequency {
    private var frequencies: IntArray
    private var cumulative: IntArray?
    override var total: Int = 0


    init {
        val frequency = FFrequency(CONST)
        frequencies = IntArray(frequency.limit)
        total = 0
        for (i in frequencies.indices) {
            val x = frequency[i]
            frequencies[i] = x
            total += x
        }
        cumulative = null
    }

    override val limit: Int
        get() = frequencies.size

    override fun get(symbol: Int) = frequencies[symbol]

    override fun set(symbol: Int, frequency: Int) {
        val temp = total - frequencies[symbol]
        total = temp + frequency
        frequencies[symbol] = frequency
        cumulative = null
    }

    override fun inc(symbol: Int) {
        total += 1
        frequencies[symbol]++
        cumulative = null
    }


    override fun down(symbol: Int): Int {
        if (cumulative == null) init()
        return cumulative!![symbol]
    }

    override fun top(symbol: Int): Int {
        if (cumulative == null) init()
        return cumulative!![symbol + 1]
    }

    private fun init() {
        cumulative = IntArray(frequencies.size + 1)
        var sum = 0
        for (i in frequencies.indices) {
            sum += frequencies[i]
            cumulative!![i + 1] = sum
        }
    }

    companion object {
        const val CONST = 257
    }
}