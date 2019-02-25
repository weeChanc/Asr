package com.weechan.asr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.weechan.asr.utils.AudioRecorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static android.view.MotionEvent.ACTION_UP;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private PopupWindow po;
    private WaveView popupWave;
    private FloatingActionButton fab;

    private List<Record> records = new ArrayList<>();
    private List<Short> all = new CopyOnWriteArrayList<>();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        initView();

        fab.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
//                startRecord();
                new Thread(()->{
                    long start =  System.currentTimeMillis();
                    Analyze.analyze(Environment.getExternalStorageDirectory().getPath() + "/SA1_.wav");
                    runOnUiThread(()->{
                        Toast.makeText(this, System.currentTimeMillis() - start + "  ", Toast.LENGTH_SHORT).show();
                    });
                }).start();

            }

            if (action == ACTION_UP) {
//                stopRecord();
            }

            return true;
        });

    }

    private void startRecord() {
        all = new CopyOnWriteArrayList<>();
        po.showAtLocation(findViewById(R.id.container), Gravity.CENTER, 0, 0);
        records.add(new Record(2));
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(records.size() - 1); //一个bug,必须滚下去先,否则新加入的不显示 原因未知
        AudioRecorder.getInstant().startRecord(new AudioRecorder.Listener() {
            @Override
            public void onDataAvaliable(byte[] data) {
                all.addAll(AudioRecorder.toShortArray(data, 20));
                popupWave.setWaves(all, true);
            }

            @Override
            public void onPause() {

            }
        });

    }

    private void stopRecord() {
        po.dismiss();
        AudioRecorder.getInstant().stop();
        records.get(records.size() - 1).setWaves(all);
        records.add(new Record("题目: " + (int) (Math.random() * 100), 1));
        popupWave.setWaves(null, false);
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        records.add(new Record("题目: " + (int) (Math.random() * 100), 1));
        adapter = new MyAdapter(records);
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        View WaveWrap = View.inflate(this, R.layout.popup_audio, null);
        po = new PopupWindow(WaveWrap);
        po.setWidth(WRAP_CONTENT);
        po.setHeight(240);
        popupWave = WaveWrap.findViewById(R.id.wave);
        popupWave.setWaves(all, true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        WaveView.pause = false;
                        adapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        WaveView.pause = true;
                        break;

                }
            }
        });

    }
}
