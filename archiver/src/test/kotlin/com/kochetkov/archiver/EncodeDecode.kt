package com.kochetkov.archiver

import com.kochetkov.archiver.app.Main
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class EncodeDecode {
    @Test
    fun `have to encode decode correct testtxt`(){
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/test.txt"))
    }

    @Test
    fun `have to encode decode correct testtxt2`(){
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/test2.txt"))
    }

    @Test
    fun `have to encode decode correct bib`(){
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/bib"))
    }


    private fun testFile(file: File) {
        val tempDirectory = Paths.get("tempTestDirectory_1").apply { toFile().mkdirs() }
        val tempFile = Files.createTempFile(Paths.get("."),"ggwp-encode__",".txt")
        val tempFile2 = Files.createTempFile(Paths.get("."),"ggwp-decode__",".txt")
        try {
            Main.main(listOf("encode", file.absolutePath.toString(), tempFile.toAbsolutePath().toString()).toTypedArray())
            Main.main(listOf("decode", tempFile.toAbsolutePath().toString(), tempFile2.toAbsolutePath().toString()).toTypedArray())
            Assert.assertArrayEquals(file.readBytes(), tempFile2.toFile().readBytes())
        } finally {
            tempDirectory.toFile().deleteRecursively()
        }
    }
}