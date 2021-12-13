package com.kochetkov.archiver

import com.kochetkov.archiver.app.Main
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.io.File

class EncodeDecode {
    @Test
    @Ignore
    fun `have to encode decode correct testtxt`() {
//        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/test.txt"))
    }

    private fun testFile(file: File) {
        val tempFile = File("__${file.name}_encode_.txt").also { it.delete(); it.createNewFile() }.toPath()
        val tempFile2 = File("__${file.name}_decode_.txt").also { it.delete(); it.createNewFile() }.toPath()

        Main.main(listOf(file.absolutePath.toString(), tempFile.toAbsolutePath().toString(), "encode").toTypedArray())
        Main.main(listOf(tempFile.toAbsolutePath().toString(), tempFile2.toAbsolutePath().toString(), "decode").toTypedArray())
        Assert.assertArrayEquals(file.readBytes(), tempFile2.toFile().readBytes())
    }
}