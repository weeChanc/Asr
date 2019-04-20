package com.weechan.asr.base

import android.content.Context
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BaseViewHolder<T>(itemView: View, val type: Int, adapter: BaseAdapter<T>) : RecyclerView.ViewHolder(itemView) {
    private val map = SparseArray<View>()
    val context: Context = itemView.context

    fun <T : View> getView(id: Int): T {
        var view = map.get(id)
        view = view ?: itemView.findViewById(id)
        map.put(id, view)
        return view as T
    }

    fun <T : View> Int.toView(): T {
        return getView<T>(this) as T
    }
}