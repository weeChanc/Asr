package com.weechan.asr.utils.permission

import androidx.appcompat.app.AppCompatActivity

/**
 * Created by jimji on 2017/9/12.
 */
@Suppress("LeakingThis")
open class PermissionCompatActivity : AppCompatActivity() {
    private val permissionMan = PermissionMan(this)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionMan.onResult()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
