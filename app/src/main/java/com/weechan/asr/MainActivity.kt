package com.weechan.asr

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.weechan.asr.utils.album.AlbumPickerActivity
import com.weechan.asr.utils.permission.Permission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AlbumPickerActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adaptee
    private lateinit var fab: FloatingActionButton
    private lateinit var model: SoundModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun startRecord() {
//        po.showAtLocation(findViewById(R.id.container), Gravity.CENTER, 0, 0)
//        model.start { popupWave.setWaves(model.getDatas(), true) }
    }

    private fun stopRecord() {
//        adapter.addWavesInActivePos(model.getDatas())
        adapter.notifyDataSetChanged()
        model.stop()
//        popupWave.setWaves(null, true)
//        po.dismiss()
    }

    fun init() {
        GlobalScope.launch(Dispatchers.Main) {
            val r1 = suspendCoroutine<Boolean> {
                Permission.STORAGE.get(this@MainActivity) { r ->
                    it.resume(r)
                }
            }

            val r2 = suspendCoroutine<Boolean> {
                Permission.AUDIO.get(this@MainActivity) { r ->
                    it.resume(r)
                }
            }

            if (r1 && r2) {
                model = SoundModel()
                GlobalScope.async { model.init() }.await()
                initView()
            } else {
                init()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        adapter = Adaptee(model.sources)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }


}
