package com.kochetkov.archiver

import java.io.File
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.*
import kotlin.math.log2
import kotlin.math.pow

class Solve(val mode: String, val input: File, val output: File) {
    private val BLOCK_SIZE = 65530

    fun solve() {
        println("input file: ${input.absolutePath}")
        println("output file: ${output.absolutePath}")
        if (mode == "encode") {
            encode()
        } else {
            decode()
        }
    }

    class MyComparator(val input: ByteArray) : Comparator<Int> {
        override fun compare(a: Int, b: Int): Int {
            for (i in input.indices) {
                val result = compareBytes(element(input, a, i), element(input, b, i))
                if (result != 0) return result
            }
            return 0
        }

        private fun element(input: ByteArray, shift: Int, index: Int): Byte {
            val i = shift + index
            return if (i >= input.size) input[i - input.size] else input[i]
        }

        private fun compareBytes(x: Byte, y: Byte): Int = if (x < y) -1 else if (x == y) 0 else 1
    }

    private fun decode() {
        println("start read indexes")
        var textByte = Files.readAllBytes(input.toPath())
        val indexAmount = ByteBuffer.wrap(textByte.toMutableList().subList(0, 4).toByteArray()).int
        var indexRead = ByteBuffer.wrap(textByte.toMutableList().subList(4, 8).toByteArray()).int
        textByte = textByte.drop(8).toByteArray()
        val indList = mutableListOf<Int>()
        println("start bwt indexes")
//        for (ind in 0 until indexAmount) {
//            var indexRead = ByteBuffer.wrap(textByte.toMutableList().subList(ind * 4, ind * 4 + 4).toByteArray()).int
        if (indexRead < 0) {
            indexRead += 65536
        }
        indList.add(indexRead)
//        }
        textByte = textByte


        println("start convertByteArrayToBiteList")
        val biteList = convertByteArrayToBiteList(textByte)
        println("start fromMonocode")
        var intListRes = fromMonocode(biteList)
        println("start decode mtf")
        intListRes = fromMtf(intListRes)
        println("start decode bwt")

//        val resList = mutableListOf<Byte>()

        val resList2 = ByteArray(intListRes.map { it.toByte() }.toByteArray().size)
        decodeBWT(intListRes.map { it.toByte() }.toByteArray(), indexRead, resList2)
//        intListRes.map { it.toByte() }.toByteArray().toList().chunked(BLOCK_SIZE).forEachIndexed { ind, chunk ->
//            resList.addAll(fromBwt(chunk.toByteArray(), indList[ind]).toList())
//        }

//        val res = resList.toByteArray()
        val res = resList2
        output.writeBytes(res)
        println("Complete decode!")
    }


    private fun element(input: ByteArray, shift: Int, index: Int): Byte {
        val i = shift + index
        return if (i >= input.size) input[i - input.size] else input[i]
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

    fun encodeBWT(input: ByteArray, output: ByteArray): Int {
        val shifts = Array(input.size) { it }
        shifts.sortWith(MyComparator(input))


        for (i in shifts.indices) {
            output[i] = element(input, shifts[i], input.size - 1)
        }

        return shifts.indexOfFirst { it == 0 }
    }

    private fun convertByteArrayToBiteList(byteArray: ByteArray): List<Boolean> {
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
        }

        return listInt
    }

    fun decodeBWT(input: ByteArray, num: Int, output: ByteArray) {
        val lessSimilar = IntArray(input.size)
        val elementAmount = IntArray(256)
        val lessAmount = IntArray(256)
        var minElement = 255
        var maxElement = 0
        for (i in input.indices) {
            val value = input[i].fixInt()
            minElement = Integer.min(minElement, value)
            maxElement = Integer.max(maxElement, value)
            lessSimilar[i] = elementAmount[value]++
        }
        var lessSum = 0
        for (i in minElement..maxElement) {
            lessAmount[i] = lessSum
            lessSum += elementAmount[i]
        }
        output[output.lastIndex] = input[num]
        var lastNum = num
        for (i in 0 until input.size - 1) {
            val prev = output.lastIndex - i
            lastNum = lessAmount[output[prev].fixInt()] + lessSimilar[lastNum]
            output[prev - 1] = input[lastNum]
        }
    }


    private fun encode() {
        val indexes = mutableListOf<Int>()
        val newInts = mutableListOf<Int>()

        val textByte = Files.readAllBytes(input.toPath())
        val newByteArr = ByteArray(textByte.size)
        println("start bwt")
        val index = encodeBWT(textByte, newByteArr)
        newInts.addAll(newByteArr.toList().map { it.toInt() })
//        textByte = newByteArr
//        textByte.toList().chunked(BLOCK_SIZE).forEach { chunk ->
//            val res = convertByBwt(chunk.toByteArray())
//            newInts.addAll(res.first.toList().map { it.toInt() })
//            indexes.add(res.second)
//        }

        println("start write indexes")
        output.writeBytes(ByteBuffer.allocate(4).putInt(indexes.size).array())
        output.appendBytes(ByteBuffer.allocate(4).putInt(index).array())
//        indexes.forEach {
//            output.appendBytes(ByteBuffer.allocate(4).putInt(it).array())
//        }

        println("start mtf")
        val listMtfIndexes = convertByMtf(newInts)
        println("start monocode")
        val bitList = convertToMonotoneCode(listMtfIndexes)
        println("start writing")
        writeBitList(bitList.toMutableList())
        println("Complete encode!")
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

    private fun Byte.fixInt(): Int = 128 + this.toInt()
}