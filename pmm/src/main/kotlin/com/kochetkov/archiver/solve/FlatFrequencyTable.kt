package com.kochetkov.archiver.solve

import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException

/*
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */ /**
 * An immutable frequency table where every symbol has the same frequency of 1.
 * Useful as a fallback model when no statistics are available.
 */
class FlatFrequencyTable(numSyms: Int) : FrequencyTable {
    /**
     * Returns the number of symbols in this table, which is at least 1.
     * @return the number of symbols in this table
     */
    /**
     * Returns the total of all symbol frequencies, which is
     * always equal to the number of symbols in this table.
     * @return the total of all symbol frequencies, which is `getSymbolLimit()`
     */
    override val total: Int

    override val symbolLimit: Int
        get() = total

    /*---- Methods ----*/
    /**
     * Returns the frequency of the specified symbol, which is always 1.
     * @param symbol the symbol to query
     * @return the frequency of the symbol, which is 1
     * @throws IllegalArgumentException if `symbol` &lt; 0 or `symbol`  `getSymbolLimit()`
     */
    override fun get(symbol: Int): Int {
        checkSymbol(symbol)
        return 1
    }

    /**
     * Returns the sum of the frequencies of all the symbols strictly below
     * the specified symbol value. The returned value is equal to `symbol`.
     * @param symbol the symbol to query
     * @return the sum of the frequencies of all the symbols below `symbol`, which is `symbol`
     * @throws IllegalArgumentException if `symbol` &lt; 0 or `symbol`  `getSymbolLimit()`
     */
    override fun getLow(symbol: Int): Int {
        checkSymbol(symbol)
        return symbol
    }

    /**
     * Returns the sum of the frequencies of the specified symbol and all
     * the symbols below. The returned value is equal to `symbol + 1`.
     * @param symbol the symbol to query
     * @return the sum of the frequencies of `symbol` and all symbols below, which is `symbol + 1`
     * @throws IllegalArgumentException if `symbol` &lt; 0 or `symbol`  `getSymbolLimit()`
     */
    override fun getHigh(symbol: Int): Int {
        checkSymbol(symbol)
        return symbol + 1
    }

    // Returns silently if 0 <= symbol < numSymbols, otherwise throws an exception.
    private fun checkSymbol(symbol: Int) {
        require(!(symbol < 0 || symbol >= total)) { "Symbol out of range" }
    }

    /**
     * Returns a string representation of this frequency table. The format is subject to change.
     * @return a string representation of this frequency table
     */
    override fun toString(): String {
        return "FlatFrequencyTable=" + total
    }

    /**
     * Unsupported operation, because this frequency table is immutable.
     * @param symbol ignored
     * @param freq ignored
     * @throws UnsupportedOperationException because this frequency table is immutable
     */
    override fun set(symbol: Int, freq: Int) {
        throw UnsupportedOperationException()
    }

    /**
     * Unsupported operation, because this frequency table is immutable.
     * @param symbol ignored
     * @throws UnsupportedOperationException because this frequency table is immutable
     */
    override fun increment(symbol: Int) {
        throw UnsupportedOperationException()
    }
    /*---- Constructor ----*/ /**
     * Constructs a flat frequency table with the specified number of symbols.
     * @param numSyms the number of symbols, which must be at least 1
     * @throws IllegalArgumentException if the number of symbols is less than 1
     */
    init {
        require(numSyms >= 1) { "Number of symbols must be positive" }
        total = numSyms
    }
}