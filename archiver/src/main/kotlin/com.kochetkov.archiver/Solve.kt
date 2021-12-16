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
//        val indexAmount = ByteBuffer.wrap(textByte.toMutableList().subList(0, 4).toByteArray()).int
        var indexRead = ByteBuffer.wrap(textByte.toMutableList().subList(0, 4).toByteArray()).int
//        textByte = textByte.drop(8).toByteArray()
        textByte = textByte.drop(4).toByteArray()
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



        var intListRes = readBytesFromRLE(textByte)
//        println("start convertByteArrayToBiteList")
//        val biteList = convertByteArrayToBiteList(textByte)
//        println("start fromMonocode")
//        var intListRes = fromMonocode(biteList)
        println("start decode mtf")
        intListRes = fromMtf(intListRes)
        println("start decode bwt")

//        val l = textByte.map { it.toInt() }.toList()
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

    fun readBytesFromRLE(byteArray: ByteArray): List<Int> {
//        var file2 = File("decode_stat_obratno.txt")
//        var sb2 = StringBuilder()


        val list = mutableListOf<Int>()
        var i = 0
        while (i < byteArray.size) {
            if (i + 1 < byteArray.size) {
                if (byteArray[i] != byteArray[i + 1]) {
                    list.add(byteArray[i].toInt().correctInt())
//                    sb2.append("${byteArray[i].toInt()}:1 ")
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

//        file2.writeText(sb2.toString())

//        var file = File("decode_obratno.txt")
//        println(file.toPath().toAbsolutePath())
//        val sb = StringBuilder()

//        list.forEach {
//            print("$it ")
//            sb.append("$it ")
//        }
//        file.writeText(sb.toString())

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
//        val newList = mutableListOf<Int>()
//
//        var counter = 0
//        while (counter < ints.size) {
//            var n = ints[counter]
//            for (i in 1..n) {
//                newList.add(ints[counter + 1])
//            }
//            counter += 2
//        }


        val alphabet = generateSequence(0) { if (it < 256) (it + 1) else null }.take(256).toMutableList()

        val resList = mutableListOf<Int>()
        var alpValue: Int
//        newList.forEach { index ->
//            val ind = index - 1
//            alpValue = alphabet[ind]
//            resList.add(alpValue)
//            alphabet.removeAt(ind)
//            alphabet.add(0, alpValue)
//        }

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
//            var newInt = 0
//            newInt += if (!biteList[counter])     (1 shl 0) else 0
//            newInt += if (!biteList[counter + 1]) (1 shl 1) else 0
//            newInt += if (!biteList[counter + 2]) (1 shl 2) else 0
//            newInt += if (!biteList[counter + 3]) (1 shl 3) else 0
//            newInt += if (!biteList[counter + 4]) (1 shl 4) else 0
//            newInt += if (!biteList[counter + 5]) (1 shl 5) else 0
//            newInt += if (!biteList[counter + 6]) (1 shl 6) else 0
//            newInt += if (!biteList[counter + 7]) (1 shl 7) else 0
//
//            newInt += if (!biteList[counter + 8])  (1 shl 8) else 0
//            newInt += if (!biteList[counter + 9])  (1 shl 9) else 0
//            newInt += if (!biteList[counter + 10]) (1 shl 10) else 0
//            newInt += if (!biteList[counter + 11]) (1 shl 11) else 0
//            newInt += if (!biteList[counter + 12]) (1 shl 12) else 0
//            newInt += if (!biteList[counter + 13]) (1 shl 13) else 0
//            newInt += if (!biteList[counter + 14]) (1 shl 14) else 0
//            newInt += if (!biteList[counter + 15]) (1 shl 15) else 0
//
//            counter += 16
//            listInt.add(newInt)
//
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
//        output.writeBytes(ByteBuffer.allocate(4).putInt(indexes.size).array())
        output.writeBytes(ByteBuffer.allocate(4).putInt(index).array())
//        indexes.forEach {
//            output.appendBytes(ByteBuffer.allocate(4).putInt(it).array())
//        }

        println("start mtf")
        val listMtfIndexes = convertByMtf(newInts)
        println("start monocode")

        val bitList = convertToMonotoneCode2(listMtfIndexes)
        output.appendBytes(bitList.toByteArray())


//        val bitList = convertToMonotoneCode(listMtfIndexes)
//        println("start writing")
//        writeBitList(bitList.toMutableList())
//        println("Complete encode!")
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


    private fun convertToMonotoneCode2(ints: List<Int>): List<Byte> {
//        println("_______1________")
//        var file = File("encode_tuda.txt")
//        var sb = StringBuilder()
//        println(file.toPath().toAbsolutePath())
//        ints.forEach {
//            print("$it ")
//            sb.append("$it ")
//        }
//        file.writeText(sb.toString())


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

        println()
//        var file2 = File("encode_stat_tuda.txt")
//        var sb2 = StringBuilder()
//        println(file2.toPath().toAbsolutePath())
//        println("_______2________")
//        newList.forEach { (f, s) ->
//            print("$f:$s ")
//            sb2.append("$f:$s ")
//        }
//        file2.writeText(sb2.toString())


        println()
//        println("_______3________")
        val biteList = mutableListOf<Byte>()
        newList.forEach { (symbol, count) ->
            if (count == 1) {
                biteList.add(symbol.toByte())
            } else {
                biteList.add(symbol.toByte())
                biteList.add(symbol.toByte())
//                biteList.add(get_r2_byte(count - 2))
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
        return (intValue shr 8).toByte()
    }

    private fun get_r2_int(byte: Byte): Int {
//        if (byte.toInt() == 1) return 256
        return (byte.toInt() shl 8)
    }

    private fun get_r1_int(byte: Byte): Int {
        return if (byte.toInt() < 0) byte.toInt() + 256 else byte.toInt()
//        return byte.toInt().correctInt()
    }



    private fun doMonotone(intValue: Int): List<Boolean> {
//        val l = mutableListOf<Boolean>()
//
//        if (!((intValue and (1 shl 0)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 1)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 2)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 3)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 4)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 5)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 6)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 7)) > 0)) l.add(true) else l.add(false)
//
//        if (!((intValue and (1 shl 8)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 9)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 10)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 11)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 12)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 13)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 14)) > 0)) l.add(true) else l.add(false)
//        if (!((intValue and (1 shl 15)) > 0)) l.add(true) else l.add(false)
//
//        if (!((intValue and (1 shl 16)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 17)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 18)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 19)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 20)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 21)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 22)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 23)) > 0)) l.add(true) else l.add(false)
//
////        if (!((intValue and (1 shl 24)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 25)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 26)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 27)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 28)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 29)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 30)) > 0)) l.add(true) else l.add(false)
////        if (!((intValue and (1 shl 31)) > 0)) l.add(true) else l.add(false)
//        return l

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