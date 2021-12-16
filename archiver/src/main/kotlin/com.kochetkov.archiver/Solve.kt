package com.kochetkov.archiver

import java.io.File
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.*
import kotlin.math.log2
import kotlin.math.pow

class Solve(val mode: String, val input: File, val output: File) {
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
        var indexRead = get_r3_int(textByte[0]) + get_r2_int(textByte[1]) + get_r1_int(textByte[2])
//        var indexRead = ByteBuffer.wrap(textByte.toMutableList().subList(0, 4).toByteArray()).int
        textByte = textByte.drop(3).toByteArray()
        val indList = mutableListOf<Int>()
        println("start bwt indexes")
        indList.add(indexRead)
        textByte = textByte



        var intListRes = readBytesFromRLE(textByte)
        println("start decode mtf")
        intListRes = fromMtf(intListRes)
        println("start decode bwt")


        val resList2 = ByteArray(intListRes.map { it.toByte() }.toByteArray().size)
        decodeBWT(intListRes.map { it.toByte() }.toByteArray(), indexRead, resList2)

        val res = resList2
        output.writeBytes(arrayOf(255.toByte()).toByteArray())
        output.appendBytes(res)
        println("Complete decode!")
    }

    fun readBytesFromRLE(byteArray: ByteArray): List<Int> {
        val list = mutableListOf<Int>()
        var i = 0
        while (i < byteArray.size) {
            if (i + 1 < byteArray.size) {
                if (byteArray[i] != byteArray[i + 1]) {
                    list.add(byteArray[i].toInt().correctInt())
                    i++
                } else {
                    list.add(byteArray[i].toInt().correctInt())
                    list.add(byteArray[i].toInt().correctInt())
                    val count = get_r1_int(byteArray[i + 2])
                    for (z in 1..count) {
                        list.add(byteArray[i].toInt().correctInt())
                    }
                    i += 3
                }
            } else {
                list.add(byteArray[i].toInt().correctInt())
                i++
            }
        }

        return list
    }

    fun Int.correctInt(): Int {
        return if (this <= 0) this + 256 else this
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
        val newInts = mutableListOf<Int>()

        var textByte = Files.readAllBytes(input.toPath())
        println("FIRSTTTTTTT: ${textByte[0]}")
        textByte = textByte.drop(1).toByteArray()
        val newByteArr = ByteArray(textByte.size)
        println("start bwt")
        val index = encodeBWT(textByte, newByteArr)
        newInts.addAll(newByteArr.toList().map { it.toInt() })

        println("start write indexes")
        val b1 = get_r1_byte(index)
        val b2 = get_r2_byte(index)
        val b3 = get_r3_byte(index)
        output.writeBytes(arrayOf(b3, b2, b1).toByteArray())

        println("start mtf")
        val listMtfIndexes = convertByMtf(newInts)
        println("start monocode")

        val bitList = convertToMonotoneCode2(listMtfIndexes)
        output.appendBytes(bitList.toByteArray())
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


    private fun convertToMonotoneCode2(ints: ByteArray): List<Byte> {
        val newList = mutableListOf<Pair<Byte, Int>>()
        var curSimb = ints[0]
        var countSimb = 0

        ints.forEach {
            if (curSimb == it && countSimb <= 256) {
                countSimb++
            } else {
                newList.add(curSimb to countSimb)
                countSimb = 1
                curSimb = it
            }
        }

        newList.add(curSimb to countSimb)



        val biteList = mutableListOf<Byte>()
        newList.forEach { (symbol, count) ->
            if (count == 1) {
                biteList.add(symbol)
            } else {
                biteList.add(symbol)
                biteList.add(symbol)
                biteList.add(get_r1_byte(count - 2))
            }
            if (count > (1 shl 16)) {
                println("ERRRROR")
            }
        }

        return biteList
    }

    private fun convertToMonotoneCode2(ints: List<Int>): List<Byte> {
        val newList = mutableListOf<Pair<Int, Int>>()
        var curSimb = ints[0]
        var countSimb = 0

        ints.forEach {
            if (curSimb == it && countSimb <= 256) {
                countSimb++
            } else {
                newList.add(curSimb to countSimb)
                countSimb = 1
                curSimb = it
            }
        }

        newList.add(curSimb to countSimb)



        val biteList = mutableListOf<Byte>()
        newList.forEach { (symbol, count) ->
            if (count == 1) {
                biteList.add(symbol.toByte())
            } else {
                biteList.add(symbol.toByte())
                biteList.add(symbol.toByte())
                biteList.add(get_r1_byte(count - 2))
            }
            if (count > (1 shl 16)) {
                println("ERRRROR")
            }
        }

        return biteList
    }

    private fun convertToMonotoneCode(ints: List<Int>): List<Boolean> {
        val newList = mutableListOf<Pair<Int, Int>>()
//
//        var curSimb = ints[0]
//        var countSimb = 0
//
//        ints.forEach {
//            if (curSimb == it) {
//                countSimb++
//            } else {
//                newList.add(curSimb to countSimb)
//                countSimb = 1
//                curSimb = it
//            }
//        }
//
//        newList.add(curSimb to countSimb)
//
//
//        val biteList = mutableListOf<Byte>()
//        newList.forEach { (symbol, count) ->
//            if (count == 1) {
//                biteList.add(symbol.toByte())
//            } else {
//                biteList.add(symbol.toByte())
//                biteList.add(symbol.toByte())
//                biteList.add(get_r2_byte(count - 2))
//                biteList.add(get_r1_byte(count - 2))
//            }
//        }
//
//
//
        //        val l = mutableListOf<Boolean>()
//
//
//        newList.forEach { int ->
//            biteList.apply {
//                addAll(doMonotone(int))
//            }
//        }
//        return biteList

        val biteList = mutableListOf<Boolean>()
        ints.forEach { int ->
            biteList.apply {
                addAll(doMonotone(int))
            }
        }
        return biteList
    }


    private fun get_r1_byte(intValue: Int): Byte {
        return (intValue and ((1 shl 8) - 1)).toByte()
    }

    private fun get_r2_byte(intValue: Int): Byte {
        return ((intValue shl 16) shr 24).toByte()
    }

    private fun get_r3_byte(intValue: Int): Byte {
        return (intValue shr 16).toByte()
    }

    private fun get_r3_int(byte: Byte): Int {
        var c = if (byte.toInt() < 0) byte.toInt() + 256 else byte.toInt()
        return (c shl 16)
    }

    private fun get_r2_int(byte: Byte): Int {
        var c = if (byte.toInt() < 0) byte.toInt() + 256 else byte.toInt()
        return (c shl 8)
    }

    private fun get_r1_int(byte: Byte): Int {
        return if (byte.toInt() < 0) byte.toInt() + 256 else byte.toInt()
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