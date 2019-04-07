package com.weechan.asr.data

import android.util.SparseArray
import com.weechan.asr.NotUsed
import java.io.File

@NotUsed
data class PHN(val text : String , val spell : SparseArray<String>){
    companion object {

        @NotUsed
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

        @NotUsed
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
                        return@map PHN(text, spell)
                    }
        }

    }
}

