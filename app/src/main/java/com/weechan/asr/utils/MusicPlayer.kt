package com.weechan.asr.utils

import android.media.MediaPlayer
import android.R
import android.net.Uri
import android.os.Environment
import com.weechan.asr.App


class MusicPlayer {

    companion object {
        private val player = MusicPlayer()
        fun getInstant() = player
    }

    private val player = MediaPlayer()
    private var onComplete: MutableList<(() -> Unit)> = mutableListOf()
    private var onStart: MutableList<(() -> Unit)> = mutableListOf()
    var isPlaying: Boolean = false
        get() = player.isPlaying

    fun addOnCompleteListener(block: (() -> Unit)) {
        this.onComplete.add(block)
    }

    fun addOnStartListener(block: (() -> Unit)) {
        onStart.add(block)
    }

    @Synchronized
    fun play(path: String) {
        player.reset()
        player.setDataSource(path)
        player.prepareAsync()
        onStart.forEach { it.invoke() }
        player.setOnPreparedListener {
            it.start()
        }
        player.setOnCompletionListener {
            onComplete.forEach { it.invoke() }
        }
    }

    @Synchronized
    fun reset(){
        player.reset()
        onComplete.forEach { it.invoke() }
    }
}