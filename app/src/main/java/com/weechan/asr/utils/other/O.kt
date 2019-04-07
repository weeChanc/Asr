package com.weechan.asr.utils.other

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.weechan.asr.App

/**
 * Created by jimji on 2017/9/15.
 */
object O {
    val mainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }//主线程
    var mToast: Toast? = null
    var mMsg: Any? = null
}

fun Any.inUiThread(run: () -> Unit) = O.mainThreadHandler.post(run)

fun Any.showToast(msg: Any) {

    inUiThread {
        if (null == O.mToast || msg.toString() !== O.mMsg!!.toString()) {
            if (null == O.mToast) {
                O.mToast = Toast.makeText(App.app, msg.toString(), Toast.LENGTH_SHORT)
            } else {
                O.mToast!!.setText(msg.toString())
            }
            O.mMsg = msg
        }
        O.mToast?.show()
    }
}

fun Any.toast() = showToast(this)

fun Any.toast(any : Any) = showToast(" $any"  )
