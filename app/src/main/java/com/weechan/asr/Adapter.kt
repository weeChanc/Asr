package com.weechan.asr

import android.annotation.SuppressLint
import com.weechan.asr.base.BaseAdapter
import com.weechan.asr.base.MultiEntity
import com.weechan.asr.data.SoundSource

@SuppressLint("ClickableViewAccessibility")
class Adapter(var sources: List<MultiEntity<SoundSource>>) : BaseAdapter<SoundSource>(sources) {

    companion object {
        val TYPE_PRE = 1;
        val TYPE_NEW = 2;
    }

    init {
        addType(TypeNew(this))
        addType(TypePre(this))
    }

}
