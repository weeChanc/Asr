package com.weechan.asr.base

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


abstract class BaseAdapter<T>(val data: List<MultiEntity<T>>) : RecyclerView.Adapter<BaseViewHolder<T>>() {

    var context: Context? = null

    init {
        refreshIndex()
    }

    fun refreshIndex() {
        data.forEachIndexed { index, entry ->
            data.get(index).__index = index
        }
    }

    private fun <T> T?.ifNotNull(block: (self: T) -> Unit) {
        if (this != null) {
            block.invoke(this)
        }
    }

    fun getVal(pos: Int) = data.takeIf { pos >= headerMap.size() && pos < headerMap.size() + data.size }?.get(pos)


    private fun transformPosition(pos: Int): Int {
        return pos - headerMap.size()
    }

    private val map = SparseArray<Type<T>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {

        if (context == null) context = parent.context

        val view = LayoutInflater.from(parent.context).inflate(map.get(viewType).layoutId, parent, false);
        val holder = BaseViewHolder(view, viewType, this)
        onTypeViewHolderCreated(view, viewType, holder)

        return holder
    }

    private fun onTypeViewHolderCreated(view: View, viewType: Int, holder: BaseViewHolder<T>) {

        setUpGlobalItemClick(view, holder.adapterPosition)

        map.get(viewType).ifNotNull { type ->

            type.onViewHolderCreated(holder, viewType);

            type._itemClick.ifNotNull {
                view.setOnClickListener {
                    type._itemClick?.invoke(holder.adapterPosition, view,
                            getVal(holder.adapterPosition))
                }
            }

            type._itemLongClick.ifNotNull {
                view.setOnClickListener {
                    type._itemLongClick?.invoke(holder.adapterPosition,
                            getVal(holder.adapterPosition))
                }
            }
        }
    }


    private fun setUpGlobalItemClick(view: View, pos: Int) {
        _globalItemLongClick.ifNotNull {
            view.setOnLongClickListener { v ->
                it.invoke(pos, getVal(pos))
                true
            }
        }
        _globalItemClick.ifNotNull {
            view.setOnClickListener { _ -> it.invoke(pos, data.get(pos)) }
        }
    }


    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        val type = getItemViewType(holder.adapterPosition)
        map.get(type)?.let {
            it.onBindViewHolder(holder)
            val data = getVal(holder.adapterPosition)?.entity
            if (data != null)
                it.onBindViewHolder(holder, data)
        }
    }


    private var _globalItemLongClick: ((position: Int, data: MultiEntity<T>?) -> Unit)? = null
    private var _globalItemClick: ((position: Int, data: MultiEntity<T>?) -> Unit)? = null
    fun setOnItemLongClickListener(block: (position: Int, data: MultiEntity<T>?) -> Unit) = { _globalItemLongClick = block }
    fun setOnItemClickListener(block: (position: Int, data: MultiEntity<T>?) -> Unit) {
        _globalItemClick = block
    }


    override fun getItemCount() = data.size + headerMap.size() + footerMap.size()


    companion object {
        @JvmStatic
        private val HEADER_ITEM = 1
        @JvmStatic
        private val FOOTER_ITEM = 2
    }

    private val headerMap = SparseArray<Type<T>>()
    private val footerMap = SparseArray<Type<T>>()
    fun addType(type: Type<T>) {
        map.put(type.typeId, type)
        if (type.__itemType__ == HEADER_ITEM) {
            headerMap.put(type.typeId, type)
        } else if (type.__itemType__ == FOOTER_ITEM) {
            footerMap.put(type.typeId, type)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position < headerMap.size()) {
            return headerMap.valueAt(position).typeId;
        } else if (position >= data.size + headerMap.size()) {
            return footerMap.valueAt(position - headerMap.size() - data.size).typeId
        }
        return data[position - headerMap.size()].typeId
    }

}