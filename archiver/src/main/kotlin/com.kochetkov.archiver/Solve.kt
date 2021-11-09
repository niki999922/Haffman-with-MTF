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

    private fun encode() {
        var inputText = ""
        input.bufferedReader().use {
            inputText = it.readText()
        }
        println("input text: $inputText")
        var bwt = bwt(inputText)
        println(bwt)
        println("decoded text: ${decodeBwt(bwt)}")


//        bwt = BWT("BCABAAA", 1)
        val mtf = mtf(bwt.text)
        println("mtd text: ${mtf.intList}")
        println("mtd alph: ${mtf.alphavit}")
        println("deco mtf: ${decodeMtf(mtf)}")



        FileOutputStream(output).use { writer ->
            mtf.intList.forEach {
                val charCode = it.toString().first().code
                println("____ $charCode")
                println("____ ${charCode.toString(2)}")
                val tmp = log2(charCode.toFloat()).toInt()
                val firstPart = "1".repeat(tmp) + "0"
                println("first: ${firstPart}")
                val second = (charCode - 2.0.pow(tmp).toInt()).toString(2)
                println("second: ${second}")
                val res = firstPart + second
                println("res: ${res}")
                writer.write(res.toByteArray())
            }
        }
    }

    private fun mtf(text: String): MTF {
        var index: Int
        val beginAlph = text.toSortedSet().toMutableList()
        val alphavit = beginAlph.toMutableList()
        val resList = mutableListOf<Int>()
        text.forEach { char ->
            index = alphavit.indexOf(char)
            resList.add(index)
            alphavit.removeAt(index)
            alphavit.add(0, char)
        }

        return MTF(resList, beginAlph)
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
        val tree = cycleShift(inputText)
        val lastColumnText = StringBuilder()
        var numberText = 0
        tree.forEachIndexed { index, elem ->
            lastColumnText.append(elem.last())
            if (elem == inputText) numberText = index
        }

        tree.forEach {
            println(it)
        }

        return BWT(lastColumnText.toString(), numberText)
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

    private fun decode() {

    }

    data class BWT(val text: String, val index: Int)

    data class MTF(val intList: List<Int>, val alphavit: List<Char>)
}