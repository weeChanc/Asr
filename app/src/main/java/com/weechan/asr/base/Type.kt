package com.weechan.asr.base

import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes

interface IAdapter<T> {
    fun onViewHolderCreated(holder: BaseViewHolder<T>, viewType: Int)
    fun onBindViewHolder(holder: BaseViewHolder<T>)
    fun onBindViewHolder(holder: BaseViewHolder<T>, data: T)
    fun notifyItemChange(item: MultiEntity<T>)
    fun insertItem(item: MultiEntity<T>)
    fun deleteItem(item: MultiEntity<T>)
}

abstract class Type<T>(@LayoutRes var layoutId: Int, var typeId: Int, private val adapter: BaseAdapter<T>) : IAdapter<T> {
    var _itemLongClick: ((pos: Int, data: MultiEntity<T>?) -> Unit)? = null
    var _itemClick: ((pos: Int, v: View, data: MultiEntity<T>?) -> Unit)? = null

    fun setOnItemLongClickListener(block: (pos: Int, data: MultiEntity<T>?) -> Unit) = { _itemLongClick = block }
    fun setOnItemClickListener(block: (pos: Int, v: View, data: MultiEntity<T>?) -> Unit) {
        _itemClick = block
    }

    companion object {
        @JvmStatic
        private val NORMAL_ITEM = 0
        @JvmStatic
        private val HEADER_ITEM = 1
        @JvmStatic
        private val FOOTER_ITEM = 2
    }

    var __itemType__: Int = NORMAL_ITEM

    fun asFooter(): Type<T> {
        __itemType__ = FOOTER_ITEM
        return this
    }

    fun asHeader(): Type<T> {
        __itemType__ = HEADER_ITEM
        return this
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>) {

    }

    override fun onViewHolderCreated(holder: BaseViewHolder<T>, viewType: Int) {

    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, data: T) {

    }

    override fun notifyItemChange(item: MultiEntity<T>) {
        adapter.notifyItemChanged(item.__index)
        Log.e("TAG", "" + item.__index)

    }

    override fun insertItem(item: MultiEntity<T>) {

    }

    override fun deleteItem(item: MultiEntity<T>) {

    }


}

