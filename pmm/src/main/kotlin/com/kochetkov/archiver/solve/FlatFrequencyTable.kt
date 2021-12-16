package com.kochetkov.archiver.solve

import java.lang.UnsupportedOperationException

class FlatFrequencyTable(override val total: Int) : FrequencyTable {
    override val symbolLimit: Int
        get() = total

    override fun get(symbol: Int): Int {
        return 1
    }

    override fun getLow(symbol: Int): Int {
        return symbol
    }

    override fun getHigh(symbol: Int): Int {
        return symbol + 1
    }


    override fun set(symbol: Int, freq: Int) {
        throw UnsupportedOperationException()
    }

    override fun increment(symbol: Int) {
        throw UnsupportedOperationException()
    }
}