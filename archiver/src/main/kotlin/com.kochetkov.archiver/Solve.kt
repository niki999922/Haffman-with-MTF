package com.kochetkov.archiver

import java.io.*
import java.lang.Math.pow
import java.nio.file.Files
import kotlin.math.log2
import kotlin.math.pow


data class Solve(val mode: String, val input: File, val output: File) {
    fun solve() {
        println("Used mode: \'encode\'")
        println("input file: ${input.absolutePath}")
        println("output file: ${output.absolutePath}")
        if (mode == "encode") {
            encode()
            decode()
        } else {
            decode()
        }
    }


    private fun decode() {
        println("try decode")
        var counter = 0
        val resMessage = StringBuilder()
        output.bufferedReader().use { reader ->
            while (true) {
                val readValue = reader.read()
                if (readValue == -1) {
                    break
                }
                val char = readValue.toChar()
                if (char == '1') {
                    counter++
                } else {
                    val leftPart = pow(2.0, counter.toDouble()).toInt()
                    val sb = StringBuilder()
                    while (counter > 0) {
                        sb.append(reader.read().toChar())
                        counter--
                    }
                    val rightPart = Integer.parseInt(sb.toString(), 2)
                    val sum = leftPart + rightPart

                    val character = sum.toChar()
                    resMessage.append(character)
//                    println("___")
//                    println("left: $leftPart   right: $rightPart")
//                    println("Char: $character")
//                    println("char code: ${character.code}")
                }
            }
            println(resMessage)
//            decodeMtf(MTF())
        }
    }

    private fun encode() {
        var textByte = Files.readAllBytes(input.toPath())
        val bwt = bwt(textByte)
        val mtf = mtf(bwt)
        monotoneCode(mtf)
    }

    private fun monotoneCode(mtf: MTF) {
        output.writeBytes(mtf.byteArray)
    }

    private fun monotone(input: Int): String {
        val code = input.toString().first().code //like 49 for '1'
        val base = log2(code.toFloat()).toInt()
        val leftPart = "1".repeat(base) + "0"
        val rightPart = (code - 2.0.pow(base).toInt()).toString(2)
        return leftPart + rightPart
//                println("____ $charCode")
//                println("____ ${charCode.toString(2)}")
//                println("first: ${firstPart}")
//                println("second: ${second}")
    }

    private fun mtf(bwt: BWT): MTF {
//    var text = bwt.byteArray.toString()
        var index: Int

        val alphabet = generateSequence(0.toByte()) { if (it < 256) (it + 1).toByte() else null }.toMutableList()
//    val beginAlphabet = text.toSortedSet().toMutableList()


        val resList = mutableListOf<Byte>()
        bwt.byteArray.plus(bwt.index.toByte()).forEach { byte ->
            index = alphabet.indexOfFirst { value ->
                value == byte
            }
            resList.add(index.toByte())
            alphabet.removeAt(index)
            alphabet.add(0, byte)
        }

        return MTF(resList.toByteArray())
    }

    private fun decodeMtf(mtf: MTF): String {
        var char: Char
        val alphavit = mtf.alphavit.toMutableList()
        val sb = StringBuilder()
        mtf.intList.forEach { ind ->
            char = alphavit[ind]
            sb.append(char)
            alphavit.removeAt(ind)
            alphavit.add(0, char)
        }

        return sb.toString()
    }

    private fun cycleShift(byteArray: ByteArray): MutableList<ByteArray> {
        return mutableListOf<ByteArray>().apply {
            var counter = byteArray.size - 1
            while (counter >= 0) {
                val byteArrTmp = ByteArray(byteArray.size)
                System.arraycopy(byteArray, counter, byteArrTmp, 0, byteArray.size - counter)
                System.arraycopy(byteArray, 0, byteArrTmp, byteArray.size - counter, counter)
//                byteArray.substring(0, counter)
//                add(byteArray.sub(counter, byteArray.length) + byteArray.substring(0, counter))
                add(byteArrTmp)
                counter--
            }
        }.sortedWith { o1, o2 ->
            o1.toString().compareTo(o2.toString())
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

    private fun decodeBwt(bwt: BWT): String {
        val list = mutableListOf<String>()
        bwt.text.forEach {
            list.add(it.toString())
        }
        list.sort()
        for (i in 0 until bwt.text.length - 1) {
            for (index in 0 until bwt.text.length) {
                list[index] = bwt.text[index] + list[index]
            }
            list.sort()
        }

        list.forEach {
            println(it)
        }

        return list[bwt.index]
    }
}

data class BWT(val byteArray: ByteArray, val index: Int)

data class MTF(val byteArray: ByteArray)


class BiteList {
    val bites = mutableListOf<Boolean>()
}