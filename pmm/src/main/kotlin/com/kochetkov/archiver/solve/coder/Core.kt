package com.kochetkov.archiver.solve.coder

import com.kochetkov.archiver.solve.table.CFrequency

abstract class Core(numBits: Int) {
    val stateBits: Int
    val range: Long
    val hRange: Long
    val qRange: Long
    val mask: Long
    var low: Long
    var high: Long

    init {
        stateBits = numBits
        range = 1L shl stateBits
        hRange = range ushr 1
        qRange = hRange ushr 1
        mask = range - 1
        low = 0
        high = mask
    }

    protected abstract fun shift()
    protected abstract fun under()

    protected fun update(frequency: CFrequency, symbol: Int) {
        val range = high - low + 1

        val newLow = low + (frequency.down(symbol).toLong()) * range / (frequency.total.toLong())
        val newHigh = low + (frequency.top(symbol).toLong()) * range / (frequency.total.toLong()) - 1

        low = newLow
        high = newHigh
        while (low xor high and hRange == 0L) {
            shift()
            low = low shl 1 and mask
            high = high shl 1 and mask or 1
        }

        while (low and high.inv() and qRange != 0L) {
            under()
            low = low shl 1 xor hRange
            high = high xor hRange shl 1 or hRange or 1
        }
    }
}