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
    fun `have to encode decode correct testtxt`(){
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/test.txt"))
    }

    @Test
    @Ignore
    fun `have to encode decode correct testtxt ya`(){
        println(239.toByte())
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
//        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/bib"))
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

    //staff    80K Nov 11 02:18 ____bib_encode_11896677895073819960.txt
    // 512K  <-  751K book1
    // 395K  <-  597K book2
    // 268K  <-  368K news
    //  35K  <-  52K paper1
    //  53K  <-  80K paper2
    //  25K  <-  39K prog_
    //  37K  <-  70K progl
    //  27K  <-  48K progp
    //  11B  <-  7B test.txt
    //  782B <-  5.1K test2.txt
    //  57K  <-  91K trans

    //a.kochetkov  staff   109K Jul  7  2003 bib
    //  751K Jul  7  2003 book1
    //  597K Jul  7  2003 book2
    //  368K Jul  7  2003 news
    //   52K Jul  7  2003 paper1
    //   80K Jul  7  2003 paper2
    //   39K Jul  7  2003 progc
    //   70K Jul  7  2003 progl
    //   48K Jul  7  2003 progp
    //    7B Nov 11 01:53 test.txt
    //  5.1K Nov 11 02:04 test2.txt
    //   91K Jul  7  2003 trans

    @Test
    @Ignore
    fun `have to encode decode correct not friend`(){
        listOf("pic","obj2","geo","obj1").parallelStream().forEach {
            testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/$it"))
        }
//        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/bib"))
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
    fun `have to encode decode correct not friend 3`(){
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/pic"))
    }

    @Test
    fun `have to encode decode correct not friend 4`(){
        testFile(File("/Users/nikita.kochetkov/Haffman-with-MTF/calgarycorpus/geo"))
    }

    @Test
    @Ignore
    fun `test monot`() {
        val tempFile1 = Files.createTempFile("____",".txt").toFile()
        val tempFile2 = Files.createTempFile("____",".txt").toFile()
        val solve = Solve("encode", tempFile1, tempFile2)

        val res = solve.monotone(0)
//        val res2 = solve.monotone(255.toByte())
//        val res3 = solve.monotone(254.toByte())
        println(res)
//        println(res2)
//        println(res3)
//        Assert.assertArrayEquals(listOf())
    }

    @Test
    @Ignore
    fun `test demonot`() {
        val tempFile1 = Files.createTempFile("____",".txt").toFile()
        val tempFile2 = Files.createTempFile("____",".txt").toFile()
        val solve = Solve("encode", tempFile1, tempFile2)

        val res = BiteList()
        res.bites.addAll(mutableListOf(true, true, true, true, true, true, true, true, false))
        println(res.bites)
        println(res.toRealByteArray().contentToString())
//        Assert.assertArrayEquals(listOf())
    }

    private fun testFile(file: File) {
        val tempDirectory = Paths.get("tempTestDirectory_1").apply { toFile().mkdirs() }
        val tempFile = Files.createTempFile(Paths.get("."),"____${file.name}_encode_",".txt")
        val tempFile2 = Files.createTempFile(Paths.get("."),"__${file.name}_decode_",".txt")
        try {
            Main.main(listOf("encode", file.absolutePath.toString(), tempFile.toAbsolutePath().toString()).toTypedArray())
            Main.main(listOf("decode", tempFile.toAbsolutePath().toString(), tempFile2.toAbsolutePath().toString()).toTypedArray())
            Assert.assertArrayEquals(file.readBytes(), tempFile2.toFile().readBytes())
//        } catch (e: Exception) {
//            System.err.println("Exception!!!!!!!!1 in ${file.name}")
        } finally {
            tempDirectory.toFile().deleteRecursively()
        }
    }
}

//txt
//80K Nov 11 02:12 bib_encode_7595410503757217923.txt
//0B Nov 11 02:12 book1_decode_6371985817469629193.txt
//512K Nov 11 02:12 book1_encode_6640625558323308048.txt
//0B Nov 11 02:12 book2_decode_18024279544129907987.txt
//395K Nov 11 02:12 book2_encode_3649300515872563220.txt
//kochetkov  staff   320B Nov  8 22:28 build
//nikita.kochetkov  staff   967B Nov  9 01:12 build.gradle.kts
//0B Nov 11 02:12 geo_decode_3525605956302350446.txt
//406B Nov 11 02:12 geo_encode_9688245296268732159.txt
//0B Nov 11 02:12 news_decode_1916330027425589482.txt
//268K Nov 11 02:12 news_encode_11810370452791092534.txt
//0B Nov 11 02:12 obj1_decode_1731912115477875402.txt
//89B Nov 11 02:12 obj1_encode_9362808414309869696.txt
//0B Nov 11 02:12 obj2_decode_3333212009257198306.txt
//972B Nov 11 02:12 obj2_encode_5145131490920239399.txt
//0B Nov 11 02:12 paper1_decode_14906406799761276677.txt
//35K Nov 11 02:12 paper1_encode_15135721833904612417.txt
//0B Nov 11 02:12 paper2_decode_11918862912577986152.txt
//53K Nov 11 02:12 paper2_encode_2336750456395285825.txt
//0B Nov 11 02:12 pic_decode_1361448615168630964.txt
//2.0K Nov 11 02:12 pic_encode_1290911579493273350.txt
//0B Nov 11 02:12 progc_decode_10303911969716924867.txt
//25K Nov 11 02:12 progc_encode_4901175857299705855.txt
//0B Nov 11 02:12 progl_decode_4562876575039561438.txt
//37K Nov 11 02:12 progl_encode_17658651886597235489.txt
//0B Nov 11 02:12 progp_decode_5697362632056814458.txt
//27K Nov 11 02:12 progp_encode_3520563160060702373.txt
//kochetkov  staff   128B Nov  8 22:26 src
//7B Nov 11 02:12 test.txt_decode_1193898799990471854.txt
//11B Nov 11 02:12 test.txt_encode_3785172776621200392.txt
//5.1K Nov 11 02:12 test2.txt_decode_996956795008027665.txt
//782B Nov 11 02:12 test2.txt_encode_2564308382771195745.txt
//0B Nov 11 02:12 trans_decode_17279183032077743856.txt
//57K Nov 11 02:12 trans_encode_12523326599261916274.txt