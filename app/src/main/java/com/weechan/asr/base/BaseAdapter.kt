package com.weechan.asr.base

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView


abstract class BaseAdapter<T>(private val data: List<MultiEntity<T>>) : RecyclerView.Adapter<BaseAdapter<T>.ViewHolder>() {

    private fun <T> T?.ifNotNull(block: (self: T) -> Unit) {
        if (this != null) {
            block.invoke(this)
        }
    }

    private fun List<MultiEntity<T>>.takeValidValue(pos: Int) = data.takeIf { pos >= headerSize && pos < headerSize + data.size }?.get(pos)


    private val map = SparseArray<Type<T>>()
    private var headerSize = 0
    private var footerSize = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        println(map)
        println(viewType)
        val view = LayoutInflater.from(parent.context).inflate(map.get(viewType).layoutId, parent, false);
        val holder = ViewHolder(view, viewType)


        _globalItemLongClick.ifNotNull {
            view.setOnLongClickListener { v ->
                it.invoke(holder.adapterPosition, data.takeValidValue(holder.adapterPosition))
                true
            }
        }
        _globalItemClick?.ifNotNull { view.setOnClickListener { _ -> it.invoke(holder.adapterPosition, data.get(holder.adapterPosition)) } }

        map.get(viewType).ifNotNull { type ->

            type._onCreate?.invoke(holder)

            type._itemClick.ifNotNull {
                view.setOnClickListener {
                    type._itemClick?.invoke(view, holder.adapterPosition,
                            data.takeValidValue(holder.adapterPosition))
                }
            }

            type._itemLongClick.ifNotNull {
                view.setOnClickListener {
                    type._itemLongClick?.invoke(holder.adapterPosition,
                            data.takeValidValue(holder.adapterPosition))
                }
            }
        }

        return holder
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val type = getItemViewType(holder.adapterPosition)
        map.get(type)?._onBind?.invoke(holder, data.takeValidValue(holder.adapterPosition)?.entity)
    }


    inner class ViewHolder(itemView: View, val type: Int) : RecyclerView.ViewHolder(itemView) {
        private val map = SparseArray<View>()
        val context: Context = itemView.context
        val entity: T
            get() = data.get(adapterPosition).entity

        val adapter: BaseAdapter<T>
            get() = this@BaseAdapter

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

    private var _globalItemLongClick: ((position: Int, data: MultiEntity<T>?) -> Unit)? = null
    private var _globalItemClick: ((position: Int, data: MultiEntity<T>?) -> Unit)? = null
    fun setOnItemLongClickListener(block: (position: Int, data: MultiEntity<T>?) -> Unit) = { _globalItemLongClick = block }
    fun setOnItemClickListener(block: (position: Int, data: MultiEntity<T>?) -> Unit) {
        _globalItemClick = block
    }


    override fun getItemCount() = data.size + headerSize + footerSize

    fun addType(type: Type<T>) {
        map.put(type.typeId, type)
    }


    private val headerMap = SparseArray<Type<T>>()
    private val footerMap = SparseArray<Type<T>>()

    fun addHeader(type: Type<T>) {
        map.put(type.typeId, type)
        headerMap.put(type.typeId, type)
        headerSize++;
    }

    fun addFooter(type: Type<T>) {
        map.put(type.typeId, type);
        footerMap.put(type.typeId, type);
        footerSize++;
    }

    override fun getItemViewType(position: Int): Int {
        if (position < headerSize) {
            return headerMap.valueAt(position).typeId;
        } else if (position >= data.size + headerSize) {
            return footerMap.valueAt(position - headerSize - data.size).typeId
        }
        return data[position - headerSize].typeId
    }


}