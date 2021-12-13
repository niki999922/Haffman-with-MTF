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
        require(!(numBits < 1 || numBits > 62)) { "State size out of range" }
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
        if (low >= high || low and stateMask != low || high and stateMask != high) throw AssertionError("Low or high out of range")
        val range = high - low + 1
        if (range < minimumRange || range > fullRange) throw AssertionError("Range out of range")

        val total = freqs.total.toLong()
        val symLow = freqs.getLow(symbol).toLong()
        val symHigh = freqs.getHigh(symbol).toLong()
        require(symLow != symHigh) { "Symbol has zero frequency" }
        require(total <= maximumTotal) { "Cannot code symbol because total is too large" }

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