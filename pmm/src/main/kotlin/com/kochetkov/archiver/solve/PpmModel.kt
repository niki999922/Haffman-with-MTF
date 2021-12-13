package com.kochetkov.archiver.solve

import java.lang.AssertionError

internal class PpmModel(order: Int, symbolLimit: Int, escapeSymbol: Int) {
    @JvmField
	val modelOrder: Int
    private val symbolLimit: Int
    private val escapeSymbol: Int
    @JvmField
	var rootContext: Context? = null
    @JvmField
	val orderMinus1Freqs: FrequencyTable

    fun incrementContexts(history: IntArray, symbol: Int) {
        if (modelOrder == -1) return
        require(!(history.size > modelOrder || symbol < 0 || symbol >= symbolLimit))
        var ctx = rootContext
        ctx!!.frequencies.increment(symbol)
        var i = 0
        for (sym in history) {
            val subctxs = ctx!!.subcontexts ?: throw AssertionError()
            if (subctxs[sym] == null) {
                subctxs[sym] = Context(symbolLimit, i + 1 < modelOrder)
                subctxs[sym]!!.frequencies.increment(escapeSymbol)
            }
            ctx = subctxs[sym]
            ctx!!.frequencies.increment(symbol)
            i++
        }
    }

    class Context(symbols: Int, hasSubctx: Boolean) {
        @JvmField
		val frequencies: FrequencyTable
        @JvmField
		val subcontexts: Array<Context?>?

        init {
            frequencies = SimpleFrequencyTable(IntArray(symbols))
            subcontexts = if (hasSubctx) arrayOfNulls(symbols) else null
        }
    }

    init {
        require(!(order < -1 || symbolLimit <= 0 || escapeSymbol < 0 || escapeSymbol >= symbolLimit))
        modelOrder = order
        this.symbolLimit = symbolLimit
        this.escapeSymbol = escapeSymbol
        if (order >= 0) {
            rootContext = Context(symbolLimit, order >= 1)
            rootContext!!.frequencies.increment(escapeSymbol)
        } else rootContext = null
        orderMinus1Freqs = FlatFrequencyTable(symbolLimit)
    }
}