package com.weechan.asr.utils

import android.media.MediaPlayer
import android.R
import android.net.Uri
import android.os.Environment
import com.weechan.asr.App


class MusicPlayer{
    companion object {
        val player = MediaPlayer()

        @Synchronized
        fun play(path : String){
            player.reset()
            player.setDataSource(path)
            player.prepareAsync()
            player.setOnPreparedListener {
                it.start()
            }
        }
    }
}