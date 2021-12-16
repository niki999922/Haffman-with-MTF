package com.kochetkov.archiver.solve.table

interface Frequency {
    val symbolLimit: Int
    val total: Int

    operator fun get(symbol: Int): Int
    operator fun set(symbol: Int, freq: Int)

    fun increment(symbol: Int)
    fun getLow(symbol: Int): Int
    fun getHigh(symbol: Int): Int
}