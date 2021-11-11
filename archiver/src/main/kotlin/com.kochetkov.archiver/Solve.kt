package com.kochetkov.archiver

import java.io.File
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.*
import kotlin.math.log2
import kotlin.math.pow


data class Solve(val mode: String, val input: File, val output: File) {
    private val testFile = File("decoded-text.txt")

    fun solve() {
        println("Used mode: \'$mode\'")
        println("input file: ${input.absolutePath}")
        println("output file: ${output.absolutePath}")
        if (mode == "encode") {
            encode()
//            decode()
        } else {
            decode()
        }
    }


    private fun decode() {
        var textByte = Files.readAllBytes(input.toPath())
//        val index = textByte.toMutableList().subList(0, 4).toByteArray()
        val index = ByteBuffer.wrap(textByte.toMutableList().subList(0, 4).toByteArray()).int
        var indList = mutableListOf<Int>()
        for (ind in 0 until index) {
            var indexRead = ByteBuffer.wrap(textByte.toMutableList().subList(ind + 4, ind + 5).toByteArray()).get().toInt()
            if (indexRead < 0) {
                indexRead += 256
            }
            indList.add(indexRead)
        }
        textByte = textByte.drop(index + 4).toByteArray()
        val mtf = deMonotoneCode(textByte)
        println("__________Decode_____")
//        printArray(mtf.byteArray)
//        println("numbers: ${mtf.byteArray.contentToString()}")
        var decodedMTF = decodeMtf(mtf)
        var biteArr = mutableListOf<Byte>()
        print("index list for polina love ($index): ${indList}")
        decodedMTF.toList().chunked(255).forEachIndexed { ind, chunk ->
            val bwt = BWT(chunk.toByteArray(), indList[ind])
            biteArr.addAll(decodeBwt(bwt).toList())
        }
//        val bwt = BWT(decodeMtf(mtf), index)
        println("start bwt")
//        println("mtf -> bwt: " + bwt.byteArray.contentToString())
//        printArray(bwt.byteArray)
//        val res = decodeBwt(bwt)
        val res = biteArr.toByteArray()
//        printArray(res)
        output.writeBytes(res)
    }

    private fun deMonotoneCode(byteArray: ByteArray): MTF {
//        println("decode monotone:")
        val biteList = BiteList()
//            println(String.format("%8s", Integer.toBinaryString((byteArray[1] and 0xFF.toByte()).toInt()).replace(' ', '0')))
        val bitSet = BitSet.valueOf(byteArray)
        val bytTmp = mutableListOf<Boolean>()
        for (index in 0 until bitSet.length()) {
            if (index % 8 == 0) {
                bytTmp.reversed().forEach { biteList.bites.add(it)}
                bytTmp.clear()
                bytTmp.add(bitSet[index])
            } else {
                bytTmp.add(bitSet[index])
            }
        }
        while (bytTmp.size < 8) {
            bytTmp.add(false)
        }
        bytTmp.reversed().forEach { biteList.bites.add(it)}
        bytTmp.clear()



//        println("biteList decode: ${biteList.bites.chunked(8).joinToString(" ") { it.joinToString("") { if (it) "1" else "0" } }}")
        return MTF(biteList.toRealByteArray())
    }

    private fun printArray(arr: ByteArray) {
        for (i in arr.indices) {
            print(arr[i].toChar())
        }
        println()
    }

    private fun encode() {
        var textByte = Files.readAllBytes(input.toPath())
//        printArray(textByte)
        println("start bwt")
        val indexes = mutableListOf<Int>()
        val newBytes = mutableListOf<Byte>()
        textByte.toList().chunked(255).forEach {
            val res = bwt(it.toByteArray())
            newBytes.addAll(res.byteArray.toList())
            indexes.add(res.index)
        }
        println("index listtt: ${indexes}")
        output.writeBytes(transformIndexToByte(indexes.size))
        indexes.forEach {
            output.appendBytes(listOf(it.toByte()).toByteArray())
//            output.appendBytes(ByteBuffer.allocate(1).putInt(it.toByte()).array())
        }
        textByte = newBytes.toByteArray()
//        output.appendBytes()
//        val bwt = bwt(textByte)
//        println("index: ${bwt.index}")
//        println("bwt -> mtf: " + textByte.contentToString())
//        printArray(textByte)
//        println("--------------------")
//        println(bwt.index)
        println("start mtf")
        val mtf = mtf(BWT(textByte, 0))
//        println("Uncode")
//        printArray(mtf.byteArray)
//        println(mtf.byteArray.contentToString())
//        printArray(mtf.byteArray)
//        println("start write output")
        monotoneCode(mtf)
    }

    private fun monotoneCode(mtf: MTF) {
//        output.writeBytes(mtf.byteArray)
//        println("monotipe:")
        val biteList = BiteList()
//        println("numbers: ${mtf.byteArray.contentToString()}")
//        print("converterByteee: ")
        mtf.byteArray.forEach { byte ->
            biteList.bites.apply {
//                println("byte: $byte")
                val l = monotone(byte)
//                print("${l.joinToString("") {if (it) "1" else "0"}} ")
                addAll(l)
            }
        }
        println()
//        print("biteList encode last: ")
//        printArray(biteList.toByteArray())
//        println("index: $index ___ byte arr: ${transformIndexToByte(index).contentToString()}")
//        output.writeBytes(transformIndexToByte(index))
        output.appendBytes(biteList.toByteArray())

//        println("biteList encode: ${biteList.bites.chunked(8).joinToString(" ") { it.joinToString("") { if (it) "1" else "0" } }}")
//        biteList.toByteArray()
    }

    fun monotone(byte: Byte): List<Boolean> {
        val x = byte.toInt()
        val base = if (byte.toInt() == 0) {
            8 //111111110
            return mutableListOf(true, true, true, true, true, true, true, true, false)
        } else {
//            byte.myLog()
            log2(byte.toFloat()).toInt() //1111110 000011
        }
        print("byte: ${byte.toFloat()} ")
        println("log2: ${base}")
        val leftPart = "1".repeat(base) + "0"

        val rightPart = (byte).toString(2).removeRange(0,1)
//        val rightPart2 = (byte - 2.0.pow(base).toInt()).toString(2)
        return mutableListOf<Boolean>().apply {
            (leftPart + rightPart).forEach {
                add(it == '1')
            }
        }
    }

    fun Byte.myLog(): Int {
        if (this.toInt() < 2) return 0
        if (this.toInt() < 4) return 1
        if (this.toInt() < 8) return 2
        if (this.toInt() < 16) return 3
        if (this.toInt() < 32) return 4
        if (this.toInt() < 64) return 5
        if (this.toInt() < 128) return 6
        if (this.toInt() < 256) return 7
        return 7
    }

    private fun mtf(bwt: BWT): MTF {
        var index: Int

        val alphabet = generateSequence(0.toByte()) { if (it.toInt() < 256) (it.toInt() + 1).toByte() else null }.take(256).toMutableList()


        val resList = mutableListOf<Byte>()
//        bwt.byteArray.plus(transformIndexToByte(bwt.index)).forEach { byte ->
        println("____________")
        bwt.byteArray.forEach { byte ->
            index = alphabet.indexOfFirst { value ->
                value == byte
            }
            print("$index  '${byte.toChar()}' '${if (byte.toInt() < 0) byte.toInt() + 256 else byte.toInt()}' | ")
            resList.add((index + 1).toByte())
            alphabet.removeAt(index)
            alphabet.add(0, byte)
        }
        println("____________")

        return MTF(resList.toByteArray())
    }

    private fun transformIndexToByte(index: Int): ByteArray {
        return ByteBuffer.allocate(4).putInt(index).array()
    }

    private fun decodeMtf(mtf: MTF): ByteArray {
        val alphabet = generateSequence(0.toByte()) { if (it.toInt() < 256) (it.toInt() + 1).toByte() else null }.take(256).toMutableList()

        val resList = mutableListOf<Byte>()
        var index: Int
        var alpValue: Byte
        mtf.byteArray.forEach { byte ->
            index = byte.toInt() - 1
            if (byte == 0.toByte()) {
                index = 255
            }
            alpValue = alphabet[index]
            resList.add(alpValue)
            alphabet.removeAt(index)
            alphabet.add(0, alpValue)
        }
//        index = ByteBuffer.wrap(resList.subList(resList.size - 4)).get
        return resList.toByteArray()
    }

    private fun cycleShift(byteArray: ByteArray): MutableList<ByteArray> {
        return mutableListOf<ByteArray>().apply {
            var counter = 0
//            println("__________")

            while (counter < byteArray.size) {
                //ABACABA
                val byteArrTmp = ByteArray(byteArray.size)
                System.arraycopy(byteArray, counter, byteArrTmp, 0, byteArray.size - counter)
                System.arraycopy(byteArray, 0, byteArrTmp, byteArray.size - counter, counter)
//                byteArray.substring(0, counter)
//                add(byteArray.sub(counter, byteArray.length) + byteArray.substring(0, counter))
//                printArray(byteArrTmp)
                add(byteArrTmp)
                counter++
            }
//            println("__________")
        }.sortedWith { o1, o2 ->
            Arrays.compare(o1, o2)
        }.toMutableList()
    }

    private fun bwt(byteArray: ByteArray): BWT {
        val matrix = cycleShift(byteArray)
        val biteArrayBuilder = ByteArray(matrix.size)
        var shiftId = 0

        matrix.forEachIndexed { index, elem ->
            biteArrayBuilder[index] = elem.last()
            if (elem.contentEquals(byteArray)) shiftId = index
        }

        return BWT(biteArrayBuilder, shiftId)
    }

    private fun decodeBwt(bwt: BWT): ByteArray {
        var list2 = mutableListOf<MutableList<Byte>>()
        bwt.byteArray.forEach {
            list2.add(mutableListOf(it))
        }
        list2 = list2.sortedWith { o1, o2 ->
            Arrays.compare(o1.toByteArray(), o2.toByteArray())
        }.toMutableList()

        for (i in 0 until bwt.byteArray.size - 1) {
            for (index in 0 until bwt.byteArray.size) {
                list2[index].add(0, bwt.byteArray[index])
            }
            list2 = list2.sortedWith { o1, o2 ->
                Arrays.compare(o1.toByteArray(), o2.toByteArray())
            }.toMutableList()
        }

        return list2[bwt.index].toByteArray()
    }
}

data class BWT(val byteArray: ByteArray, val index: Int)

data class MTF(val byteArray: ByteArray)


class BiteList {
    val bites = mutableListOf<Boolean>()

    fun toByteArray(): ByteArray {
        while (bites.size % 8 != 0) {
            bites.add(true)
        }
        return bites.chunked(8) { chunk ->
            BigInteger(chunk.joinToString(separator = "") {
                if (it) "1" else "0"
            }, 2).toByte()
        }.toByteArray()
//        return l
    }

    fun toRealByteArray(): ByteArray {
        val byteList = mutableListOf<Byte>()
        var index = 0
        while (index < bites.size) {
            if (bites[index]) {
                var amountOne = 0
                val stringBuilderRight = StringBuilder()

                while (index < bites.size && bites[index]) {
                    amountOne++
                    index++
                }
                index++
                if (amountOne == 8) {
                    byteList.add(0.toByte())
                    continue
                }
                if (index >= bites.size) {
                    break
                }
                for (ignore in 0 until amountOne) {
                    stringBuilderRight.append(if (bites[index]) "1" else "0")
                    index++
                }

                val left = 2.0.pow(amountOne.toDouble()).toInt()
                val right = BigInteger(stringBuilderRight.toString(), 2).toInt()
                val res = (left + right).toByte()
                byteList.add(res)
            } else {
                byteList.add(BigInteger("1").toByte())
                index++
            }
        }
        return byteList.toByteArray()
    }
}