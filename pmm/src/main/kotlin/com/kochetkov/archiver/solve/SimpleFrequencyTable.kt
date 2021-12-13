package com.kochetkov.archiver.solve

import java.util.Objects
import java.lang.AssertionError
import java.lang.ArithmeticException
import java.lang.StringBuilder

class SimpleFrequencyTable : FrequencyTable {
    private var frequencies: IntArray
    private var cumulative: IntArray?
    override var total: Int

    constructor(freqs: IntArray) {
        Objects.requireNonNull(freqs)
        require(freqs.size >= 1) { "At least 1 symbol needed" }
        require(freqs.size <= Int.MAX_VALUE - 1) { "Too many symbols" }
        frequencies = freqs.clone()
        total = 0
        for (x in frequencies) {
            require(x >= 0) { "Negative frequency" }
            total = checkedAdd(x, total)
        }
        cumulative = null
    }

    constructor(freqs: FrequencyTable) {
        Objects.requireNonNull(freqs)
        val numSym = freqs.symbolLimit
        require(numSym >= 1) { "At least 1 symbol needed" }
        frequencies = IntArray(numSym)
        total = 0
        for (i in frequencies.indices) {
            val x = freqs[i]
            require(x >= 0) { "Negative frequency" }
            frequencies[i] = x
            total = checkedAdd(x, total)
        }
        cumulative = null
    }

    override val symbolLimit: Int
        get() = frequencies.size

    override fun get(symbol: Int): Int {
        checkSymbol(symbol)
        return frequencies[symbol]
    }

    override fun set(symbol: Int, freq: Int) {
        checkSymbol(symbol)
        require(freq >= 0) { "Negative frequency" }
        val temp = total - frequencies[symbol]
        if (temp < 0) throw AssertionError()
        total = checkedAdd(temp, freq)
        frequencies[symbol] = freq
        cumulative = null
    }

    override fun increment(symbol: Int) {
        checkSymbol(symbol)
        if (frequencies[symbol] == Int.MAX_VALUE) throw ArithmeticException("Arithmetic overflow")
        total = checkedAdd(total, 1)
        frequencies[symbol]++
        cumulative = null
    }


    override fun getLow(symbol: Int): Int {
        checkSymbol(symbol)
        if (cumulative == null) initCumulative()
        return cumulative!![symbol]
    }

    override fun getHigh(symbol: Int): Int {
        checkSymbol(symbol)
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
        if (sum != total) throw AssertionError()
    }

    private fun checkSymbol(symbol: Int) {
        require(!(symbol < 0 || symbol >= frequencies.size)) { "Symbol out of range" }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (i in frequencies.indices) sb.append(String.format("%d\t%d%n", i, frequencies[i]))
        return sb.toString()
    }

    companion object {
        private fun checkedAdd(x: Int, y: Int): Int {
            val z = x + y
            return if (y > 0 && z < x || y < 0 && z > x) throw ArithmeticException("Arithmetic overflow") else z
        }
    }
}