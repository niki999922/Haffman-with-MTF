package com.kochetkov.archiver.solve

import java.util.Objects
import java.lang.IllegalArgumentException
import java.lang.AssertionError
import java.lang.ArithmeticException
import java.lang.StringBuilder

/**
 * A mutable table of symbol frequencies. The number of symbols cannot be changed
 * after construction. The current algorithm for calculating cumulative frequencies
 * takes linear time, but there exist faster algorithms such as Fenwick trees.
 */
class SimpleFrequencyTable : FrequencyTable {
    // The frequency for each symbol. Its length is at least 1, and each element is non-negative.
    private var frequencies: IntArray

    // cumulative[i] is the sum of 'frequencies' from 0 (inclusive) to i (exclusive).
    // Initialized lazily. When this is not null, the data is valid.
    private var cumulative: IntArray?

    /**
     * Returns the total of all symbol frequencies. The returned value is at
     * least 0 and is always equal to `getHigh(getSymbolLimit() - 1)`.
     * @return the total of all symbol frequencies
     */
    override var total: Int

    /*---- Constructors ----*/
    /**
     * Constructs a frequency table from the specified array of symbol frequencies. There must be at least
     * 1 symbol, no symbol has a negative frequency, and the total must not exceed `Integer.MAX_VALUE`.
     * @param freqs the array of symbol frequencies
     * @throws NullPointerException if the array is `null`
     * @throws IllegalArgumentException if `freqs.length` &lt; 1,
     * `freqs.length` = `Integer.MAX_VALUE`, or any element `freqs[i]` &lt; 0
     * @throws ArithmeticException if the total of `freqs` exceeds `Integer.MAX_VALUE`
     */
    constructor(freqs: IntArray) {
        Objects.requireNonNull(freqs)
        require(freqs.size >= 1) { "At least 1 symbol needed" }
        require(freqs.size <= Int.MAX_VALUE - 1) { "Too many symbols" }
        frequencies = freqs.clone() // Make copy
        total = 0
        for (x in frequencies) {
            require(x >= 0) { "Negative frequency" }
            total = checkedAdd(x, total)
        }
        cumulative = null
    }

    /**
     * Constructs a frequency table by copying the specified frequency table.
     * @param freqs the frequency table to copy
     * @throws NullPointerException if `freqs` is `null`
     * @throws IllegalArgumentException if `freqs.getSymbolLimit()` &lt; 1
     * or any element `freqs.get(i)` &lt; 0
     * @throws ArithmeticException if the total of all `freqs` elements exceeds `Integer.MAX_VALUE`
     */
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

    /**
     * Returns the number of symbols in this frequency table, which is at least 1.
     * @return the number of symbols in this frequency table
     */

    override val symbolLimit: Int
        get() = frequencies.size

    /**
     * Returns the frequency of the specified symbol. The returned value is at least 0.
     * @param symbol the symbol to query
     * @return the frequency of the specified symbol
     * @throws IllegalArgumentException if `symbol` &lt; 0 or `symbol`  `getSymbolLimit()`
     */
    override fun get(symbol: Int): Int {
        checkSymbol(symbol)
        return frequencies[symbol]
    }

    /**
     * Sets the frequency of the specified symbol to the specified value. The frequency value
     * must be at least 0. If an exception is thrown, then the state is left unchanged.
     * @param symbol the symbol to set
     * @param freq the frequency value to set
     * @throws IllegalArgumentException if `symbol` &lt; 0 or `symbol`  `getSymbolLimit()`
     * @throws ArithmeticException if this set request would cause the total to exceed `Integer.MAX_VALUE`
     */
    override fun set(symbol: Int, freq: Int) {
        checkSymbol(symbol)
        require(freq >= 0) { "Negative frequency" }
        val temp = total - frequencies[symbol]
        if (temp < 0) throw AssertionError()
        total = checkedAdd(temp, freq)
        frequencies[symbol] = freq
        cumulative = null
    }

    /**
     * Increments the frequency of the specified symbol.
     * @param symbol the symbol whose frequency to increment
     * @throws IllegalArgumentException if `symbol` &lt; 0 or `symbol`  `getSymbolLimit()`
     */
    override fun increment(symbol: Int) {
        checkSymbol(symbol)
        if (frequencies[symbol] == Int.MAX_VALUE) throw ArithmeticException("Arithmetic overflow")
        total = checkedAdd(total, 1)
        frequencies[symbol]++
        cumulative = null
    }


    /**
     * Returns the sum of the frequencies of all the symbols strictly
     * below the specified symbol value. The returned value is at least 0.
     * @param symbol the symbol to query
     * @return the sum of the frequencies of all the symbols below `symbol`
     * @throws IllegalArgumentException if `symbol` &lt; 0 or `symbol`  `getSymbolLimit()`
     */
    override fun getLow(symbol: Int): Int {
        checkSymbol(symbol)
        if (cumulative == null) initCumulative()
        return cumulative!![symbol]
    }

    /**
     * Returns the sum of the frequencies of the specified symbol
     * and all the symbols below. The returned value is at least 0.
     * @param symbol the symbol to query
     * @return the sum of the frequencies of `symbol` and all symbols below
     * @throws IllegalArgumentException if `symbol` &lt; 0 or `symbol`  `getSymbolLimit()`
     */
    override fun getHigh(symbol: Int): Int {
        checkSymbol(symbol)
        if (cumulative == null) initCumulative()
        return cumulative!![symbol + 1]
    }

    // Recomputes the array of cumulative symbol frequencies.
    private fun initCumulative() {
        cumulative = IntArray(frequencies.size + 1)
        var sum = 0
        for (i in frequencies.indices) {
            // This arithmetic should not throw an exception, because invariants are being maintained
            // elsewhere in the data structure. This implementation is just a defensive measure.
            sum = checkedAdd(frequencies[i], sum)
            cumulative!![i + 1] = sum
        }
        if (sum != total) throw AssertionError()
    }

    // Returns silently if 0 <= symbol < frequencies.length, otherwise throws an exception.
    private fun checkSymbol(symbol: Int) {
        require(!(symbol < 0 || symbol >= frequencies.size)) { "Symbol out of range" }
    }

    /**
     * Returns a string representation of this frequency table,
     * useful for debugging only, and the format is subject to change.
     * @return a string representation of this frequency table
     */
    override fun toString(): String {
        val sb = StringBuilder()
        for (i in frequencies.indices) sb.append(String.format("%d\t%d%n", i, frequencies[i]))
        return sb.toString()
    }

    companion object {
        // Adds the given integers, or throws an exception if the result cannot be represented as an int (i.e. overflow).
        private fun checkedAdd(x: Int, y: Int): Int {
            val z = x + y
            return if (y > 0 && z < x || y < 0 && z > x) throw ArithmeticException("Arithmetic overflow") else z
        }
    }
}