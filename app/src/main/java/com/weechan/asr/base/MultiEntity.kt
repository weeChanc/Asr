package com.weechan.asr.base

import androidx.annotation.LayoutRes
import androidx.lifecycle.LiveData

data class MultiEntity<T>(var entity: T, var typeId: Int) {
    var __index = 0
}


