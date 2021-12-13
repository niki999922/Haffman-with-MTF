package com.kochetkov.archiver

import com.kochetkov.archiver.solve.PpmCompress
import com.kochetkov.archiver.solve.PpmDecompress
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.math.BigDecimal

class EncodeDecode {

    @Test
    fun `test compr magic`() {
        val tempDirectory = File("/Users/nikita.kochetkov/Haffman-with-MTF/lab2/test_case/all/test1")
        val l = tempDirectory.resolve("__baboon30_encode_.ppm")
        println(l.toPath().toAbsolutePath())
        println(l.readBytes().size)
    }

    @Test
    fun `have to encode and decode fruits`() {
        val tempDirectory = File("/Users/nikita.kochetkov/Haffman-with-MTF/lab2/test_case/fruits/test2")
        tempDirectory.resolve("compressedResults.txt").let { it.delete(); it.createNewFile() }

        val fileNames = listOf("fruits30.ppm", "fruits80.ppm")

        fileNames.stream().forEach { file ->
            testFile(tempDirectory.resolve(file))
        }
    }


    @Test
    fun `have to encode and decode all lab files`() {
        val tempDirectory = File("/Users/nikita.kochetkov/Haffman-with-MTF/lab2/test_case/all/test1")
        tempDirectory.resolve("compressedResults.txt").let { it.delete(); it.createNewFile() }

        val fileNames = listOf(
            "airplane30.ppm", "arctichare30.ppm", "baboon30.ppm", "cat30.ppm", "fruits30.ppm", "frymire30.ppm", "girl30.ppm", "lena30.ppm", "monarch30.ppm", "peppers30.ppm", "pool30.ppm", "sails30.ppm", "serrano30.ppm", "tulips30.ppm", "watch30.ppm",
            "airplane80.ppm", "arctichare80.ppm", "baboon80.ppm", "cat80.ppm", "fruits80.ppm", "frymire80.ppm", "girl80.ppm", "lena80.ppm", "monarch80.ppm", "peppers80.ppm", "pool80.ppm", "sails80.ppm", "serrano80.ppm", "tulips80.ppm", "watch80.ppm",
        )

        fileNames.parallelStream().forEach { file ->
            testFile(tempDirectory.resolve(file))
        }
    }

    private fun testFile(file: File) {
        val name = file.name.removeSuffix(".ppm")

        val original = file.parentFile.resolve("__${name}_boriginal.ppm").also { it.delete(); }
        val tempFile = file.parentFile.resolve("__${name}_encode_.ppm").also { it.delete(); it.createNewFile() }.toPath()
        val tempFile2 = file.parentFile.resolve("__${name}_decode_.ppm").also { it.delete(); it.createNewFile() }.toPath()

        file.copyTo(original)

        println("Start compress for $name")
        val startComp = System.currentTimeMillis()
        PpmCompress.main(arrayOf(file.toPath().toAbsolutePath().toString(), tempFile.toAbsolutePath().toString()))
        val endComp = System.currentTimeMillis()
        println("Complete compress for $name: ${(endComp - startComp)} ms")

        println("Start decompress for $name")
        val startDec = System.currentTimeMillis()
        PpmDecompress.main(arrayOf(tempFile.toAbsolutePath().toString(), tempFile2.toAbsolutePath().toString()))
        val endDec = System.currentTimeMillis()
        println("Complete decompress for $name: ${(endDec - startDec)} ms")

        Assert.assertArrayEquals(file.readBytes(), tempFile2.toFile().readBytes())
        println("Time spent amount for $name: ${((endDec - startDec) + (endComp - startComp))} ms")


        println("Checking on compression > 2% $name")
        val dirWith30Jpg = File("/Users/nikita.kochetkov/Haffman-with-MTF/lab2/jpeg30")
        val dirWith80Jpg = File("/Users/nikita.kochetkov/Haffman-with-MTF/lab2/jpeg80")
        val compRes = file.parentFile.resolve("compressedResults.txt")

        val originalCompressedSize = if (name.contains("30")) {
            dirWith30Jpg.resolve("$name.jpg").readBytes().size
        } else {
            dirWith80Jpg.resolve("$name.jpg").readBytes().size

        }
        val myCompressed = tempFile.toFile().readBytes().size
        val compressedProcent = BigDecimal(100).setScale(5).minus(BigDecimal(myCompressed * 100).setScale(5).div(BigDecimal(originalCompressedSize)))
        compRes.appendText("Compression for $name.jpg is $compressedProcent%\n")
        Assert.assertTrue(compressedProcent.compareTo(BigDecimal(2).setScale(5)) == 1)
    }


//    private fun Long.normalize() = toString().reversed().chunked(4).joinToString(" ").reversed()
}