package com.weechan.asr.utils

import android.content.Intent
import com.weechan.asr.utils.permission.PermissionCompatActivity

/**
 * Created by 铖哥 on 2017/11/8.
 */
open class AlbumPickerActivity : PermissionCompatActivity(){

    var albumPicker : AlbumPicker?  = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        albumPicker?.onActivityResult(requestCode,resultCode,data)
    }
}
