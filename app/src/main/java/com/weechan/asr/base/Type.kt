package com.weechan.asr.base

import android.view.View
import androidx.annotation.LayoutRes

open class Type<T>(@LayoutRes var layoutId: Int, var typeId: Int) {
        var _onCreate: (BaseAdapter<T>.ViewHolder.() -> Unit)? = null
        var _onBind: (BaseAdapter<T>.ViewHolder.(data: T) -> Unit)? = null
        var _itemLongClick: ((position: Int, data: MultiEntity<T>?) -> Unit)? = null
        var _itemClick: ((v: View, position: Int, data: MultiEntity<T>?) -> Unit)? = null

        fun setOnItemLongClickListener(block: (position: Int, data: MultiEntity<T>?) -> Unit) = { _itemLongClick = block }
        fun setOnItemClickListener(block: (v: View, position: Int, data: MultiEntity<T>?) -> Unit) {
            _itemClick = block
        }

        fun whenCreate(block: BaseAdapter<T>.ViewHolder.() -> Unit) {
            _onCreate = block
        }

        fun whenBind(block: BaseAdapter<T>.ViewHolder.(data: T) -> Unit) {
            _onBind = block
        }
    }