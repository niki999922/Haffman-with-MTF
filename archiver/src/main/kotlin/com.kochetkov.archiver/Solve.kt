package com.kochetkov.archiver

import java.io.File
import java.io.FileOutputStream
import java.lang.Math.pow
import kotlin.math.log2
import kotlin.math.pow

data class Solve(val mode:String, val input: File, val output: File) {
    fun solve() {
        println("Used mode: \'encode\'")
        println("input file: ${input.absolutePath}")
        println("output file: ${output.absolutePath}")
        if (mode == "encode") {
            encode()
        } else {
            decode()
        }
    }


    private fun decode() {
        println("try decode")
        var counter = 0
        val resMessage = StringBuilder()
        output.bufferedReader().use { reader ->
            while(true) {
                val readValue = reader.read()
                if (readValue == -1) {
                    break
                }
                val char = readValue.toChar()
                if (char == '1') {
                    counter++
                } else {
                    val f1 = pow(2.0, counter.toDouble())
                    val sb = StringBuilder()
                    while (counter > 0) {
                        sb.append(reader.read().toChar())
                        counter--
                    }
                    val sum = f1.toInt() + Integer.getInteger(sb.toString(), 2)
                    println("d1: ${f1.toInt()}   d2: ${Integer.getInteger(sb.toString(), 2)}")

                    val wasChar = sum.toChar()
                    resMessage.append(wasChar)
                    println("wasChar: $wasChar")
                    println("wasChar int: ${wasChar.toInt()}")
                    //sec = (charCode - 2.0.pow(tmp).toInt()).toString(2)
//                    val firstPart = log2(charCode.toFloat()).toInt()
//                    val firstPart2 = log2(charCode.toFloat()).toInt()
                    counter = 0

                }
            }
//            allText = reader.readText()
        }
    }

    private fun encode() {
        input.bufferedReader().use {
            val bwt = bwt(it.readText())
            val mtf = mtf(bwt.text)
            monotoneCode(mtf)
        }
    }

    private fun monotoneCode(mtf: MTF) {
        FileOutputStream(output).use { writer ->
            mtf.intList.forEach {
                val code = it.toString().first().code //like 49 for '0'
                val base = log2(code.toFloat()).toInt()
                val leftPart = "1".repeat(base) + "0"
                val rightPart = (code - 2.0.pow(base).toInt()).toString(2)
                val word = leftPart + rightPart
                writer.write(word.toByteArray())
//                println("____ $charCode")
//                println("____ ${charCode.toString(2)}")
//                println("first: ${firstPart}")
//                println("second: ${second}")
//                println("word: $word")
            }
        }
    }

    private fun mtf(text: String): MTF {
        var index: Int
        val beginAlphabet = text.toSortedSet().toMutableList()
        val alphabet = beginAlphabet.toMutableList()
        val resList = mutableListOf<Int>()
        text.forEach { char ->
            index = alphabet.indexOf(char)
            resList.add(index)
            alphabet.removeAt(index)
            alphabet.add(0, char)
        }

        return MTF(resList, beginAlphabet)
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

    private fun cycleShift(input: String): MutableList<String> {
        return mutableListOf<String>().apply {
            var counter = input.length - 1
            while (counter >= 0) {
                add(input.substring(counter, input.length) + input.substring(0, counter))
                counter--
            }
        }.sorted().toMutableList()
    }

    private fun bwt(inputText: String): BWT {
        val matrix = cycleShift(inputText)
        val sb = StringBuilder()
        var shiftId = 0

        matrix.forEachIndexed { index, elem ->
            sb.append(elem.last())
            if (elem == inputText) shiftId = index
        }

        return BWT(sb.toString(), shiftId)
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


    data class BWT(val text: String, val index: Int)

    data class MTF(val intList: List<Int>, val alphavit: List<Char>)
}