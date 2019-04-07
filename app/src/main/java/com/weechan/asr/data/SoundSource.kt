package com.weechan.asr.data

import java.io.File

data class SoundSource(
        val wav: String,
        val phn: String,
        val wrd: String,
        val sentence: Sentence
) {
    companion object {
        public fun readAllSoundSource(path: String): List<SoundSource> {
            return File(path)
                    .listFiles()
                    .map { it.absolutePath }
                    .filter { it.endsWith(".wav", true) }
                    .map {
                        val source = SoundSource(
                                it,
                                "${it.substring(0, it.length - 5)}.PHN",
                                "${it.substring(0, it.length - 5)}.WRD",
                                readSentence("${it.substring(0, it.length - 5)}.TXT")
                        )
                        return@map source
                    }
        }

        public fun readSentence(path: String): Sentence {
            val words = mutableListOf<Word>()
            var s = StringBuilder()
            File(path).readLines().forEach {
                it.split(" ").filterIndexed { index, s -> index >= 2 }.forEach {
                    words.add(Word(it))
                    s.append("$it ")
                }
            }
            return Sentence(words, s.toString())
        } 
    }
}