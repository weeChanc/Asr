package com.weechan.asr

import android.util.Log
import android.widget.TextView
import com.weechan.asr.Adapter.Companion.TYPE_NEW
import com.weechan.asr.Adapter.Companion.TYPE_PRE
import com.weechan.asr.base.BaseViewHolder
import com.weechan.asr.base.MultiEntity
import com.weechan.asr.base.Type
import com.weechan.asr.data.SoundSource
import com.weechan.asr.utils.MusicPlayer

class TypePre(private val adapter: Adapter) : Type<SoundSource>(R.layout.item_pre, TYPE_PRE, adapter) {
    var itemExpand: MultiEntity<SoundSource>? = null

    override fun onBindViewHolder(holder: BaseViewHolder<SoundSource>, data: SoundSource) {
        holder.getView<TextView>(R.id.title).text = data.sentence.content
    }

    override fun onViewHolderCreated(holder: BaseViewHolder<SoundSource>, viewType: Int) {

        setOnItemClickListener { _, v, item ->
            if (item == null) return@setOnItemClickListener
            if (itemExpand == null) {
                item.typeId = TYPE_NEW
                itemExpand = item
                notifyItemChange(item)
            } else {
                Log.e("else","${itemExpand} ${item}")
                itemExpand?.typeId = TYPE_PRE
                item.typeId = TYPE_NEW
                notifyItemChange(itemExpand!!)
                notifyItemChange(item)
                itemExpand = item
            }

            v.post {
                MusicPlayer.getInstant().play(item.entity.wav)
            }
        }
    }


}