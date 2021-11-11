package com.kochetkov.archiver

import com.kochetkov.archiver.app.Main
import org.junit.Assert
import org.junit.Ignore
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
    @Ignore
    fun `have to encode decode correct testtxt ya`(){
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/tes3.txt"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct testtxt2`(){
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/test2.txt"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct bib`(){
        listOf("bib","book1","book2","news","paper1","paper2","progc","progl","progp","test.txt","test2.txt","trans").parallelStream().forEach {
            testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/$it"))
        }
    }

    @Test
    fun `have to encode decode correct 10`() { testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/trans")) }

    @Test
    fun `have to encode decode correct 9`() { testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/progp")) }

    @Test
    fun `have to encode decode correct 8`() { testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/progl")) }

    @Test
    fun `have to encode decode correct 7`() { testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/bib")) }

    @Test
    fun `have to encode decode correct 6`() { testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/book1")) }

    @Test
    fun `have to encode decode correct 5`() { testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/book2")) }

    @Test
    fun `have to encode decode correct 4`() { testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/news")) }

    @Test
    fun `have to encode decode correct 3`() { testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/paper1")) }

    @Test
    fun `have to encode decode correct 2`() { testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/paper2")) }

    @Test
    fun `have to encode decode correct 1`() { testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/progc")) }

    @Test
    @Ignore
    fun `have to encode decode correct not friend`(){
        listOf("pic","obj2","geo","obj1").parallelStream().forEach {
            testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/$it"))
        }
    }

    @Test
    fun `have to encode decode correct not friend 1`(){
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/obj1"))
    }

    @Test
    fun `have to encode decode correct not friend 2`(){
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/obj2"))
    }

    @Test
    fun `have to encode decode correct not friend 3 aaaaaaaaaaaaaaaaa`(){
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/pic"))
    }

    @Test
     fun `have to encode decode correct not friend 4`(){
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/geo"))
    }

    private fun testFile(file: File) {
        val tempFile = File("__${file.name}_encode_.txt").also { it.delete(); it.createNewFile() }.toPath()
        val tempFile2 = File("__${file.name}_decode_.txt").also { it.delete(); it.createNewFile() }.toPath()

        Main.main(listOf(file.absolutePath.toString(), tempFile.toAbsolutePath().toString(), "encode").toTypedArray())
        Main.main(listOf(tempFile.toAbsolutePath().toString(), tempFile2.toAbsolutePath().toString(), "decode").toTypedArray())
        Assert.assertArrayEquals(file.readBytes(), tempFile2.toFile().readBytes())
    }
}