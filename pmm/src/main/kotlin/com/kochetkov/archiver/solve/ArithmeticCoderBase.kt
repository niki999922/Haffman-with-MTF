package com.kochetkov.archiver.solve

abstract class ArithmeticCoderBase(numBits: Int) {
    protected val numStateBits: Int
    private val fullRange: Long
    protected val halfRange: Long
    private val quarterRange: Long
    private val minimumRange: Long
    protected val maximumTotal: Long
    protected val stateMask: Long
    protected var low: Long
    protected var high: Long

    init {
        numStateBits = numBits
        fullRange = 1L shl numStateBits
        halfRange = fullRange ushr 1
        quarterRange = halfRange ushr 1
        minimumRange = quarterRange + 2
        maximumTotal = Math.min(Long.MAX_VALUE / fullRange, minimumRange)
        stateMask = fullRange - 1
        low = 0
        high = stateMask
    }

    protected abstract fun shift()
    protected abstract fun underflow()

    protected fun update(freqs: CheckedFrequencyTable, symbol: Int) {
        val range = high - low + 1

        val total = freqs.total.toLong()
        val symLow = freqs.getLow(symbol).toLong()
        val symHigh = freqs.getHigh(symbol).toLong()

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