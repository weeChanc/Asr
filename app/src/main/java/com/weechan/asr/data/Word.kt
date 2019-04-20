package com.weechan.asr.data

data class Word(val content: String, var score: Int = 1, var regularSpell: String = "", var yourSepll: String = "")