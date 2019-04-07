package com.weechan.asr.net

data class Resp(val word: List<String>, val err: List<Int>)

data class MResposne( val code : Int , val message: String , val data : Resp)

