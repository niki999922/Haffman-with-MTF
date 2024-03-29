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
    @Ignore
    fun `have to encode decode correct testtxt`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/test.txt"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct testtxt ya`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/tes3.txt"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct testtxt2`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/test2.txt"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct bib`() {
        listOf("bib", "book1", "book2", "news", "paper1", "paper2", "progc", "progl", "progp", "test.txt", "test2.txt", "trans").parallelStream().forEach {
            testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/$it"))
        }
    }

    @Test
    @Ignore
    fun `have to encode decode correct JPG`() {
        listOf("airplane30.jpg","arctichare30.jpg","baboon30.jpg","cat30.jpg","fruits30.jpg","frymire30.jpg","girl30.jpg","lena30.jpg","monarch30.jpg","peppers30.jpg","pool30.jpg","sails30.jpg","serrano30.jpg","tulips30.jpg","watch30.jpg").parallelStream().forEach {
            testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/lab2/jpeg30/$it"))
        }

//        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/lab2/jpeg30/airplane30.jpg"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct JPG 80`() {
        listOf("airplane80.jpg","arctichare80.jpg","baboon80.jpg","cat80.jpg","fruits80.jpg","frymire80.jpg","girl80.jpg","lena80.jpg","monarch80.jpg","peppers80.jpg","pool80.jpg","sails80.jpg","serrano80.jpg","tulips80.jpg","watch80.jpg").parallelStream().forEach {
            testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/lab2/jpeg80/$it"))
        }

//        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/lab2/jpeg30/airplane30.jpg"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct vala`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/airplane30_vala.ppm"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct airplane30`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/airplane30.jpg"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct 10`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/trans"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct 9`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/progp"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct 8`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/progl"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct 7`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/bib"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct 6`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/book1"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct 5`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/book2"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct 4`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/news"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct 3`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/paper1"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct 2`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/paper2"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct 1`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/progc"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct not friend`() {
        listOf("pic", "obj2", "geo", "obj1").parallelStream().forEach {
            testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/$it"))
        }
    }

    @Test
    @Ignore
    fun `have to encode decode correct not friend 1`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/obj1"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct not friend 2`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/obj2"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct not friend 3 aaaaaaaaaaaaaaaaa`() {
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/pic"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct not friend 4`() {
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