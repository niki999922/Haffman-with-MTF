package com.kochetkov.archiver.solve

import java.io.*
import java.util.*

object PpmDecompress {
    private const val MODEL_ORDER = 3

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size != 2) {
            System.err.println("Usage: java PpmDecompress InputFile OutputFile")
            System.exit(1)
            return
        }
        val inputFile = File(args[0])
        val outputFile = File(args[1])
        BitInputStream(BufferedInputStream(FileInputStream(inputFile))).use { `in` -> BufferedOutputStream(FileOutputStream(outputFile)).use { out -> decompress(`in`, out) } }
    }

    fun decompress(`in`: BitInputStream?, out: OutputStream) {
        val dec = ArithmeticDecoder(32, `in`!!)
        val model = PpmModel(MODEL_ORDER, 257, 256)
        var history = IntArray(0)
        while (true) {
            val symbol = decodeSymbol(dec, model, history)
            if (symbol == 256)
                break
            out.write(symbol)
            model.incrementContexts(history, symbol)
            if (model.modelOrder >= 1) {
                if (history.size < model.modelOrder) history = Arrays.copyOf(history, history.size + 1)
                System.arraycopy(history, 0, history, 1, history.size - 1)
                history[0] = symbol
            }
        }
    }

    private fun decodeSymbol(dec: ArithmeticDecoder, model: PpmModel, history: IntArray): Int {
        outer@ for (order in history.size downTo 0) {
            var ctx = model.rootContext
            for (i in 0 until order) {
                if (ctx!!.subcontexts == null) throw AssertionError()
                ctx = ctx.subcontexts!![history[i]]
                if (ctx == null) continue@outer
            }
            val symbol = dec.read(ctx!!.frequencies)
            if (symbol < 256) return symbol
        }
        return dec.read(model.orderMinus1Freqs)
    }
}