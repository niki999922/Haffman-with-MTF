package com.kochetkov.archiver.solve

import java.io.*
import java.util.*

object PpmCompress {
    private const val MODEL_ORDER = 3

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size != 2) {
            System.err.println("Usage: java PpmCompress InputFile OutputFile")
            System.exit(1)
            return
        }
        val inputFile = File(args[0])
        val outputFile = File(args[1])
        BufferedInputStream(FileInputStream(inputFile)).use { input ->
            BitOutputStream(BufferedOutputStream(FileOutputStream(outputFile))).use { output ->
                compress(input, output)
            }
        }
    }

    fun compress(`in`: InputStream, out: BitOutputStream?) {
        val enc = ArithmeticEncoder(32, out!!)
        val model = PpmModel(MODEL_ORDER, 257, 256)
        var history = IntArray(0)
        while (true) {
            val symbol = `in`.read()
            if (symbol == -1) break
            encodeSymbol(model, history, symbol, enc)
            model.incrementContexts(history, symbol)
            if (model.modelOrder >= 1) {
                if (history.size < model.modelOrder) history = Arrays.copyOf(history, history.size + 1)
                System.arraycopy(history, 0, history, 1, history.size - 1)
                history[0] = symbol
            }
        }
        encodeSymbol(model, history, 256, enc)
        enc.finish()
    }

    private fun encodeSymbol(model: PpmModel, history: IntArray, symbol: Int, enc: ArithmeticEncoder) {
        outer@ for (order in history.size downTo 0) {
            var ctx = model.rootContext
            for (i in 0 until order) {
                if (ctx!!.subcontexts == null) throw AssertionError()
                ctx = ctx.subcontexts!![history[i]]
                if (ctx == null) continue@outer
            }
            if (symbol != 256 && ctx!!.frequencies[symbol] > 0) {
                enc.write(ctx.frequencies, symbol)
                return
            }
            enc.write(ctx!!.frequencies, 256)
        }
        enc.write(model.orderMinus1Freqs, symbol)
    }
}