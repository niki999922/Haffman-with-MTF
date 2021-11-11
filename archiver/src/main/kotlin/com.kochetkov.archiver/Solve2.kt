package com.kochetkov.archiver

import java.io.File
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.*
import kotlin.math.log2
import kotlin.math.pow

class Solve2(val mode: String, val input: File, val output: File) {
    private val BLOCK_SIZE = 250
//    val tempFile = File("ttttt.txt")
    fun solve() {
        println("input file: ${input.absolutePath}")
        println("output file: ${output.absolutePath}")
//        println("Used solve2 mode: \'encode\'")
        if (mode == "encode") {
            encode()
        } else {
            decode()
        }
    }

    private fun decode() {
        println("start read indexes")
        //TODO: DONT FORGET!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        var textByte = Files.readAllBytes(input.toPath())
        val indexAmount = ByteBuffer.wrap(textByte.toMutableList().subList(0, 4).toByteArray()).int
        val indList = mutableListOf<Int>()
        println("start bwt indexes")
        for (ind in 0 until indexAmount) {
            var indexRead = ByteBuffer.wrap(textByte.toMutableList().subList(ind + 4, ind + 5).toByteArray()).get().toInt()
            if (indexRead < 0) {
                indexRead += 256
            }
            indList.add(indexRead)
        }
        textByte = textByte.drop(indexAmount + 4).toByteArray()


        println("start convertByteArrayToBiteList")
        val biteList = convertByteArrayToBiteList(textByte)
//        printBooleanArray(biteList, "AFTER READ DECODE")
        println("start fromMonocode")
        var intListRes = fromMonocode(biteList)
        println("start decode mtf")
//        println("BEFORE DECODE MTF: $intListRes")
        intListRes = fromMtf(intListRes)
//        println("AFTER DECODE MTF: $intListRes")
        println("start decode bwt")

        val resList = mutableListOf<Byte>()
        intListRes.map { it.toByte() }.toByteArray().toList().chunked(BLOCK_SIZE).forEachIndexed { ind, chunk ->
//            println("chunk: $ind of ${intListRes.size}")
//            if (ind % 1000 == 0) {
//                println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
//                println("chunk: $ind of ${intListRes.size}")
//            }
            resList.addAll(fromBwt(chunk.toByteArray(), indList[ind]).toList())
        }


        val res = resList.toByteArray()
        //TODO: DONT FORGET!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
        output.writeBytes(res)
        println("Complete decode!")
    }

    private fun fromBwt(byteArray: ByteArray, index: Int): ByteArray {
        var list2 = mutableListOf<MutableList<Byte>>()

        byteArray.forEach {
            list2.add(mutableListOf(it))
        }
        list2 = list2.sortedWith { o1, o2 ->
            Arrays.compare(o1.toByteArray(), o2.toByteArray())
        }.toMutableList()

        for (i in 0 until byteArray.size - 1) {
            for (index in byteArray.indices) {
                list2[index].add(0, byteArray[index])
            }
            list2 = list2.sortedWith { o1, o2 ->
                Arrays.compare(o1.toByteArray(), o2.toByteArray())
            }.toMutableList()
        }

        return list2[index].toByteArray()
    }

    private fun fromMtf(ints: List<Int>): List<Int> {
        val alphabet = generateSequence(0) { if (it < 256) (it + 1) else null }.take(256).toMutableList()

        val resList = mutableListOf<Int>()
        var alpValue: Int
        ints.forEach { index ->
            val ind = index - 1
            alpValue = alphabet[ind]
            resList.add(alpValue)
            alphabet.removeAt(ind)
            alphabet.add(0, alpValue)
        }

        return resList
    }

    private fun convertByteArrayToBiteList(byteArray: ByteArray): List<Boolean> {
        //TODO: не верю! (полина)
        val biteList = mutableListOf<Boolean>()
        val bytTmp = mutableListOf<Boolean>()
        val bitSet = BitSet.valueOf(byteArray)
        for (index in 0 until bitSet.length()) {
            if (index % 8 == 0) {
                bytTmp.reversed().forEach {
                    biteList.add(it)
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
            biteList.add(it)
        }
        bytTmp.clear()

        return biteList
    }

    private fun fromMonocode(biteList: List<Boolean>): List<Int> {
        val listInt = mutableListOf<Int>()
        var counter = 0
        while (counter < biteList.size) {
            if (!biteList[counter]) {
                listInt.add(1)
                counter++
                continue
            } else {
                var amountOne = 0
                while (counter < biteList.size && biteList[counter]) {
                    counter++
                    amountOne++
                }
                if (counter >= biteList.size) {
                    continue
                }
                counter++
                val intX = 2.0.pow(amountOne).toInt()
                var intY = 0
                for (pwr in (amountOne - 1) downTo 0) {
                    if (biteList[counter]) {
                        intY += (2.0.pow(pwr)).toInt()
                    }
                    counter++
                }
                listInt.add(intX + intY)
            }
//            counter++
        }

        return listInt
    }

    private fun encode() {
        val indexes = mutableListOf<Int>()
        val newInts = mutableListOf<Int>()

        val textByte = Files.readAllBytes(input.toPath())
//        println(textByte.toList())
        println("start bwt")
        textByte.toList().chunked(BLOCK_SIZE).forEach { chunk ->
            val res = convertByBwt(chunk.toByteArray())
            newInts.addAll(res.first.toList().map { it.toInt() })
            indexes.add(res.second)
        }

        println("start write indexes")
        output.writeBytes(ByteBuffer.allocate(4).putInt(indexes.size).array())
        indexes.forEach {
            output.appendBytes(listOf(it.toByte()).toByteArray())
        }

        println("start mtf")
//        println("BEFORE MTF: $newInts")
        val listMtfIndexes = convertByMtf(newInts)
//        println("AFTER MTF: $listMtfIndexes")
        println("start monocode")
        val bitList = convertToMonotoneCode(listMtfIndexes)
//        printBooleanArray(bitList, "AFTER MONOCODE")
        println("start writing")
        writeBitList(bitList.toMutableList())
    }

    private fun writeBitList(bitList: MutableList<Boolean>) {
        while (bitList.size % 8 != 0) {
            bitList.add(true)
        }
        val res = bitList.chunked(8) { chunk ->
            BigInteger(chunk.joinToString(separator = "") {
                if (it) "1" else "0"
            }, 2).toByte()
        }.toByteArray()

        output.appendBytes(res)
    }

    private fun convertToMonotoneCode(ints: List<Int>): List<Boolean> {
        val biteList = mutableListOf<Boolean>()
        ints.forEach { int ->
            biteList.apply {
                addAll(doMonotone(int))
            }
        }
        return biteList
    }

    private fun doMonotone(intValue: Int): List<Boolean> {
        val base = log2(intValue.toFloat()).toInt()
        val leftPart = "1".repeat(base) + "0"

        val rightPart = (intValue).toString(2).removeRange(0, 1)
        return mutableListOf<Boolean>().apply {
            (leftPart + rightPart).forEach {
                add(it == '1')
            }
        }
    }


    private fun convertByBwt(byteArray: ByteArray): Pair<ByteArray, Int> {
        val matrix = mutableListOf<ByteArray>().apply {
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
        val biteArrayBuilder = ByteArray(matrix.size)
        var shiftId = 0

        matrix.forEachIndexed { index, elem ->
            biteArrayBuilder[index] = elem.last()
            if (elem.contentEquals(byteArray)) shiftId = index
        }

        return biteArrayBuilder to shiftId
    }

    private fun convertByMtf(ints: List<Int>): List<Int> {
        var index: Int
        val alphabet = generateSequence(0) { if (it < 256) (it + 1) else null }.take(256).toMutableList()

        val result = mutableListOf<Int>()
        ints.forEach { intValue ->
            index = alphabet.indexOfFirst { value ->
                value == if (intValue < 0) intValue + 256 else intValue
            }
            result.add((index + 1))
            val alpValue = alphabet[index]
            alphabet.removeAt(index)
            alphabet.add(0, alpValue)
        }

        return result
    }


    private fun printBooleanArray(booleanArray: List<Boolean>, name: String) {
        println("PRINT BOOLEAN ARRAY : $name")
        println(booleanArray.joinToString("") { if (it) "1" else "0" })
    }
}