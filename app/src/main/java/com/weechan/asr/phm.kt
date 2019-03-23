package com.weechan.asr

import android.util.SparseArray
import java.io.File

data class PHN(val text : String , val spell : SparseArray<String>){
    companion object {

        fun readPHN(path: String): SparseArray<String> {
            val result = SparseArray<String>()
            val br = File(path)
                    .inputStream()
                    .buffered()
                    .bufferedReader()
            br.readLines().forEach {
                val i = it.split(" ").iterator()
                result.put((i.next().toInt() + i.next().toInt()), i.next())
            }
            return result
        }

        public fun readAllPHN(path: String): List<PHN> {
            return File(path)
                    .listFiles()
                    .map { it.absolutePath }
                    .filter { it.endsWith(".PHN", true) }
                    .map {
                        println(it)
                        val spell = readPHN(it);
                        val file = "${it.substring(0,it.length-3)}TXT"
                        val text = File(file).bufferedReader().readLine()
                        return@map PHN(text,spell)
                    }
        }



    }
}

