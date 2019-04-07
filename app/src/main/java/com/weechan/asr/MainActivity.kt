package com.weechan.asr

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tencent.bugly.crashreport.CrashReport
import com.weechan.asr.net.BASE_URL
import com.weechan.asr.utils.album.AlbumPickerActivity
import com.weechan.asr.utils.permission.Permission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AlbumPickerActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adaptee
    private lateinit var model: SoundModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

        CrashReport.initCrashReport(applicationContext, "008ec33f99", true)
        button.setOnClickListener {
            alert {
                val et = EditText(this@MainActivity)
                et.setText("172.17.66.67")
                et.setHint("172.17.66.67")
                customView = et;
                title = "请输入IP"
                this.positiveButton("OK"){
                    BASE_URL = "http://" + et.text.toString()
                }
            }.show()
        }
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

}
