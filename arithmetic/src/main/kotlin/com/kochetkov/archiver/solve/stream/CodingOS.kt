package com.kochetkov.archiver.solve.stream

import com.kochetkov.archiver.solve.util.addTo8th
import com.kochetkov.archiver.solve.util.flushBuff
import java.io.Closeable
import java.io.OutputStream

class CodingOS(val output: OutputStream) : Closeable {
    var byte = 0
    var bitsWritten = 0

    fun write(value: Int) {
        byte = byte shl 1 or value
        bitsWritten++
        flushBuff()
    }

    override fun close() {
        addTo8th()
        output.close()
    }
}