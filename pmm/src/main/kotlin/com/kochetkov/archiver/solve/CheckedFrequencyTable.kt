package com.kochetkov.archiver.solve

import java.lang.AssertionError
import java.util.Objects

/*
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */ /**
 * A wrapper that checks the preconditions (arguments) and postconditions (return value)
 * of all the frequency table methods. Useful for finding faults in a frequency table
 * implementation. However, arithmetic overflow conditions are not checked.
 */
class CheckedFrequencyTable(freq: FrequencyTable) : FrequencyTable {
    /*---- Fields ----*/ // The underlying frequency table that holds the data (not null).
    private val freqTable: FrequencyTable

    /*---- Methods ----*/
    override val symbolLimit: Int
        get() {
            val result = freqTable.symbolLimit
            if (result <= 0) throw AssertionError("Non-positive symbol limit")
            return result
        }

    override fun get(symbol: Int): Int {
        val result = freqTable[symbol]
        if (!isSymbolInRange(symbol)) throw AssertionError("IllegalArgumentException expected")
        if (result < 0) throw AssertionError("Negative symbol frequency")
        return result
    }

    override val total: Int
        get() {
            val result = freqTable.total
            if (result < 0) throw AssertionError("Negative total frequency")
            return result
        }

    override fun getLow(symbol: Int): Int {
        return if (isSymbolInRange(symbol)) {
            val low = freqTable.getLow(symbol)
            val high = freqTable.getHigh(symbol)
            if (!(0 <= low && low <= high && high <= freqTable.total)) throw AssertionError("Symbol low cumulative frequency out of range")
            low
        } else {
            freqTable.getLow(symbol)
            throw AssertionError("IllegalArgumentException expected")
        }
    }

    override fun getHigh(symbol: Int): Int {
        return if (isSymbolInRange(symbol)) {
            val low = freqTable.getLow(symbol)
            val high = freqTable.getHigh(symbol)
            if (!(0 <= low && low <= high && high <= freqTable.total)) throw AssertionError("Symbol high cumulative frequency out of range")
            high
        } else {
            freqTable.getHigh(symbol)
            throw AssertionError("IllegalArgumentException expected")
        }
    }

    override fun toString(): String {
        return "CheckedFrequencyTable ($freqTable)"
    }

    override fun set(symbol: Int, freq: Int) {
        freqTable[symbol] = freq
        if (!isSymbolInRange(symbol) || freq < 0) throw AssertionError("IllegalArgumentException expected")
    }

    override fun increment(symbol: Int) {
        freqTable.increment(symbol)
        if (!isSymbolInRange(symbol)) throw AssertionError("IllegalArgumentException expected")
    }

    private fun isSymbolInRange(symbol: Int): Boolean {
        return 0 <= symbol && symbol < symbolLimit
    }

    /*---- Constructor ----*/
    init {
        freqTable = Objects.requireNonNull(freq)
    }
}