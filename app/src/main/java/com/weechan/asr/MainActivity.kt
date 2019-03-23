package com.weechan.asr

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.weechan.asr.utils.AlbumPicker
import com.weechan.asr.utils.AlbumPickerActivity
import com.weechan.asr.utils.permission.Permission
import com.weechan.asr.utils.permission.PermissionCompatActivity
import com.weechan.asr.widget.WaveView
import kotlinx.android.synthetic.main.activity_main.*
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
    private lateinit var po: PopupWindow
    private lateinit var popupWave: WaveView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun startRecord() {
        po.showAtLocation(findViewById(R.id.container), Gravity.CENTER, 0, 0)
        model.start { popupWave.setWaves(model.getDatas(), true) }
    }

    private fun stopRecord() {
        adapter.addWavesInActivePos(model.getDatas())
        adapter.notifyDataSetChanged()
        model.stop()
        popupWave.setWaves(null, true)
        po.dismiss()


        //
        //        new Thread(() -> {
        //            long time = System.currentTimeMillis();
        //            runOnUiThread(() -> {
        //                Toast.makeText(this, "开始分 析,分析未完成请不要操作", Toast.LENGTH_SHORT).show();
        //            });
        //            File outFile = new File(curFile.getAbsolutePath() + ".wav");
        //            AudioRecorder.convertPcmToWav(curFile.getAbsolutePath(), outFile.getAbsolutePath(), 16000, 1,   16);
        //            Analyze.analyze(Environment.getExternalStorageDirectory().getPath() + "/SA2_.wav");
        //            runOnUiThread(() -> {
        //                Toast.makeText(this, "分析耗时　" + (System.currentTimeMillis() - time), Toast.LENGTH_SHORT).show();
        //                StringBuilder result = new StringBuilder();
        //                try {
        //                    FileInputStream fis = new FileInputStream(new File(getFilesDir()+"/model", "output.txt"));
        //                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        //                    String line;
        //                    while ((line = br.readLine()) != null) {
        //                        String[] two = line.split(",");
        //                        int soundIndex = Integer.parseInt(two[0]);
        //                        long pos = Long.parseLong(two[1]);
        //                        String sound = soundMap.get(soundIndex);
        //                        result.append(sound);
        //                    }
        //                } catch (FileNotFoundException e) {
        //                    e.printStackTrace();
        //                } catch (IOException e) {
        //                    e.printStackTrace();
        //                }
        //                records.add(new Record(result.toString(), 1));
        //                popupWave.setWaves(null, false);
        //                adapter.notifyDataSetChanged();
        //            });
        //        }).start();
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

        adapter = Adaptee(model.records)
        fab = findViewById(R.id.fab)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null

        val WaveWrap = View.inflate(this, R.layout.popup_audio, null)
        po = PopupWindow(WaveWrap)
        po.width = dp2px(240f)
        po.height = dp2px(120f)
        popupWave = WaveWrap.findViewById(R.id.wave)
        popupWave.mode = 0
        popupWave.setWaves(model.getDatas(), true)

        fab.setOnTouchListener { v, event ->
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                startRecord()
            }

            if (action == ACTION_UP) {
                stopRecord()
            }

            true
        }

        fab_select.setOnClickListener {
            AlbumPicker.with(this).selectedPicAndHandle {
                Log.e("Tag",it)
            }
        }

    }

    private fun dp2px(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }


}
