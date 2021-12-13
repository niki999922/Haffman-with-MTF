package com.kochetkov.archiver.solve

import java.io.*
import java.util.*

/*
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */ /**
 * Compression application using prediction by partial matching (PPM) with arithmetic coding.
 *
 * Usage: java PpmCompress InputFile OutputFile
 *
 * Then use the corresponding "PpmDecompress" application to recreate the original input file.
 *
 * Note that both the compressor and decompressor need to use the same PPM context modeling logic.
 * The PPM algorithm can be thought of as a powerful generalization of adaptive arithmetic coding.
 */
object PpmCompress {
    // Must be at least -1 and match PpmDecompress. Warning: Exponential memory usage at O(257^n).
    private const val MODEL_ORDER = 3
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        // Handle command line arguments
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

    // To allow unit testing, this method is package-private instead of private.
    @Throws(IOException::class)
    fun compress(`in`: InputStream, out: BitOutputStream?) {
        // Set up encoder and model. In this PPM model, symbol 256 represents EOF;
        // its frequency is 1 in the order -1 context but its frequency
        // is 0 in all other contexts (which have non-negative order).
        val enc = ArithmeticEncoder(32, out!!)
        val model = PpmModel(MODEL_ORDER, 257, 256)
        var history = IntArray(0)
        while (true) {
            // Read and encode one byte
            val symbol = `in`.read()
            if (symbol == -1) break
            encodeSymbol(model, history, symbol, enc)
            model.incrementContexts(history, symbol)
            if (model.modelOrder >= 1) {
                // Prepend current symbol, dropping oldest symbol if necessary
                if (history.size < model.modelOrder) history = Arrays.copyOf(history, history.size + 1)
                System.arraycopy(history, 0, history, 1, history.size - 1)
                history[0] = symbol
            }
        }
        encodeSymbol(model, history, 256, enc) // EOF
        enc.finish() // Flush remaining code bits
    }

    @Throws(IOException::class)
    private fun encodeSymbol(model: PpmModel, history: IntArray, symbol: Int, enc: ArithmeticEncoder) {
        // Try to use highest order context that exists based on the history suffix, such
        // that the next symbol has non-zero frequency. When symbol 256 is produced at a context
        // at any non-negative order, it means "escape to the next lower order with non-empty
        // context". When symbol 256 is produced at the order -1 context, it means "EOF".
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
            // Else write context escape symbol and continue decrementing the order
            enc.write(ctx!!.frequencies, 256)
        }
        // Logic for order = -1
        enc.write(model.orderMinus1Freqs, symbol)
    }
}