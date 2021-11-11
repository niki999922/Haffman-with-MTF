package com.kochetkov.archiver

import java.io.File
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.*
import kotlin.math.log2
import kotlin.math.pow


data class Solve(val mode: String, val input: File, val output: File) {
    fun solve() {
        println("Used mode: \'$mode\'")
        println("input file: ${input.absolutePath}")
        println("output file: ${output.absolutePath}")
        if (mode == "encode") {
            encode()
        } else {
            decode()
        }
    }


    private fun decode() {
        var textByte = Files.readAllBytes(input.toPath())
        val index = ByteBuffer.wrap(textByte.toMutableList().subList(0, 4).toByteArray()).int
        val indList = mutableListOf<Int>()
        for (ind in 0 until index) {
            var indexRead = ByteBuffer.wrap(textByte.toMutableList().subList(ind + 4, ind + 5).toByteArray()).get().toInt()
            if (indexRead < 0) {
                indexRead += 256
            }
            indList.add(indexRead)
        }
        textByte = textByte.drop(index + 4).toByteArray()
        val mtf = deMonotoneCode(textByte)
        val decodedMTF = decodeMtf(mtf)
        val biteArr = mutableListOf<Byte>()
        decodedMTF.toList().chunked(255).forEachIndexed { ind, chunk ->
            val bwt = BWT(chunk.toByteArray(), indList[ind])
            biteArr.addAll(decodeBwt(bwt).toList())
        }
        val res = biteArr.toByteArray()
        output.writeBytes(res)
    }

    private fun deMonotoneCode(byteArray: ByteArray): ByteArray {
        val biteList = BiteList()
        val bitSet = BitSet.valueOf(byteArray)
        val bytTmp = mutableListOf<Boolean>()
        for (index in 0 until bitSet.length()) {
            if (index % 8 == 0) {
                bytTmp.reversed().forEach {
                    biteList.bites.add(it)
                }
                bytTmp.clear()
                bytTmp.add(bitSet[index])
            } else {
                bytTmp.add(bitSet[index])
            }
        }
        while (bytTmp.size < 8) {
            bytTmp.add(false)
        }
        bytTmp.reversed().forEach {
            biteList.bites.add(it)
        }
        bytTmp.clear()
        return biteList.toRealByteArray()
    }

    private fun encode() {
        var textByte = Files.readAllBytes(input.toPath())
        val indexes = mutableListOf<Int>()
        val newBytes = mutableListOf<Byte>()
        textByte.toList().chunked(255).forEach {
            val res = bwt(it.toByteArray())
            newBytes.addAll(res.byteArray.toList())
            indexes.add(res.index)
        }
        output.writeBytes(transformIndexToByte(indexes.size))
        indexes.forEach {
            output.appendBytes(listOf(it.toByte()).toByteArray())
        }
        textByte = newBytes.toByteArray()
        val mtf = mtf(BWT(textByte, 0))
        monotoneCode(mtf)
    }

    private fun monotoneCode(mtf: MTF) {
        val biteList = BiteList()
        mtf.byteArray.forEach { byte ->
            biteList.bites.apply {
                val l = monotone(byte)
                addAll(l)
            }
        }
        output.appendBytes(biteList.toByteArray())
    }

    fun monotone(byte: Int): List<Boolean> {
        var x = byte
        if (x < 0) {
            x += 256
        }

        val base = log2(x.toFloat()).toInt()
        val leftPart = "1".repeat(base) + "0"

        val rightPart = (x).toString(2).removeRange(0,1)
        return mutableListOf<Boolean>().apply {
            (leftPart + rightPart).forEach {
                add(it == '1')
            }
        }
    }

    private fun mtf(bwt: BWT): MTF {
        var index: Int

        val alphabet = generateSequence(0.toByte()) { if (it.toInt() < 256) (it.toInt() + 1).toByte() else null }.take(256).toMutableList()

        val resList2 = mutableListOf<Int>()
        bwt.byteArray.forEach { byte ->
            index = alphabet.indexOfFirst { value ->
                value == byte
            }
            resList2.add((index + 1))
            alphabet.removeAt(index)
            alphabet.add(0, byte)
        }

        return MTF(resList2)
    }

    private fun transformIndexToByte(index: Int): ByteArray {
        return ByteBuffer.allocate(4).putInt(index).array()
    }

    private fun decodeMtf(mtf: ByteArray): ByteArray {
        val alphabet = generateSequence(0.toByte()) { if (it.toInt() < 256) (it.toInt() + 1).toByte() else null }.take(256).toMutableList()

        val resList = mutableListOf<Byte>()
        var index: Int
        var alpValue: Byte
        mtf.forEach { byte ->
            index = byte.toInt()
            if (index < 0) {
                index += 256
            }
            alpValue = alphabet[index]
            resList.add(alpValue)
            alphabet.removeAt(index)
            alphabet.add(0, alpValue)
        }
        return resList.toByteArray()
    }

    private fun cycleShift(byteArray: ByteArray): MutableList<ByteArray> {
        return mutableListOf<ByteArray>().apply {
            var counter = 0

            while (counter < byteArray.size) {
                val byteArrTmp = ByteArray(byteArray.size)
                System.arraycopy(byteArray, counter, byteArrTmp, 0, byteArray.size - counter)
                System.arraycopy(byteArray, 0, byteArrTmp, byteArray.size - counter, counter)
                add(byteArrTmp)
                counter++
            }
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

data class MTF(val byteArray: List<Int>)

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
    }

    fun toRealByteArray(): ByteArray {
        val byteList2 = mutableListOf<Int>()
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
                byteList2.add(res - 1)
            } else {
                byteList2.add(0)
                index++
            }
        }
        return byteList2.map { it.toByte() }.toByteArray()
    }
}