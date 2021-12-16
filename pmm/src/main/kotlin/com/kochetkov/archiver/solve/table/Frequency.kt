package com.kochetkov.archiver.solve.table

interface Frequency {
    val limit: Int
    val total: Int

    operator fun get(symbol: Int): Int
    operator fun set(symbol: Int, frequency: Int)

    fun inc(symbol: Int)
    fun down(symbol: Int): Int
    fun top(symbol: Int): Int
}