package com.kochetkov.archiver.solve.coder

import com.kochetkov.archiver.solve.table.CFrequency

abstract class Core(numBits: Int) {
    val numStateBits: Int
    val fullRange: Long
    val halfRange: Long
    val quarterRange: Long
    val stateMask: Long
    var low: Long
    var high: Long

    init {
        numStateBits = numBits
        fullRange = 1L shl numStateBits
        halfRange = fullRange ushr 1
        quarterRange = halfRange ushr 1
        stateMask = fullRange - 1
        low = 0
        high = stateMask
    }

    protected abstract fun shift()
    protected abstract fun underflow()

    protected fun update(freqs: CFrequency, symbol: Int) {
        val range = high - low + 1

        val total = freqs.total.toLong()
        val symLow = freqs.down(symbol).toLong()
        val symHigh = freqs.top(symbol).toLong()

        val newLow = low + symLow * range / total
        val newHigh = low + symHigh * range / total - 1
        low = newLow
        high = newHigh

        while (low xor high and halfRange == 0L) {
            shift()
            low = low shl 1 and stateMask
            high = high shl 1 and stateMask or 1
        }

        while (low and high.inv() and quarterRange != 0L) {
            underflow()
            low = low shl 1 xor halfRange
            high = high xor halfRange shl 1 or halfRange or 1
        }
    }
}