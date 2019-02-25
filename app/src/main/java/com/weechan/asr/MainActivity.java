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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);


        initView();

        fab.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                startRecord();
            }

            if (action == ACTION_UP) {
                stopRecord();
            }

            return true;
        });

    }

    private File curFile = null;
    private BufferedOutputStream out;

    private void startRecord() {
        all = new CopyOnWriteArrayList<>();
        po.showAtLocation(findViewById(R.id.container), Gravity.CENTER, 0, 0);
        records.add(new Record(2));
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(records.size() - 1); //一个bug,必须滚下去先,否则新加入的不显示 原因未知
        curFile = new File(new File(getFilesDir(), "sound-asr"), System.currentTimeMillis() + "_record.wav");

        try {
            out = new BufferedOutputStream(new FileOutputStream(curFile));
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Record failed!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        AudioRecorder.getInstant().startRecord(new AudioRecorder.Listener() {
            @Override
            public void onDataAvaliable(byte[] data)  {
                all.addAll(AudioRecorder.toShortArray(data, 120));
                popupWave.setWaves(all, true);
                try {
                    out.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPause() {

            }
        });

    }

    private void stopRecord() {
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        po.dismiss();
        AudioRecorder.getInstant().stop();
        records.get(records.size() - 1).setWaves(all);

        new Thread(() -> {
            long time = System.currentTimeMillis();
            runOnUiThread(() -> {
                Toast.makeText(this, "开始分析,分析未完成请不要操作", Toast.LENGTH_SHORT).show();
            });
            Analyze.analyze(curFile.getPath());
            runOnUiThread(() -> {
                Toast.makeText(this, "分析耗时　" + (System.currentTimeMillis() - time), Toast.LENGTH_SHORT).show();
                StringBuilder result = new StringBuilder();
                try {
                    FileInputStream fis = new FileInputStream(new File(getFilesDir(),"output.txt"));
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    String line;
                    while ((line = br.readLine()) != null ){
                            result.append(" ").append(line);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                records.add(new Record(result.toString(), 1));
                popupWave.setWaves(null, false);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void initView() {

        adapter = new MyAdapter(records);
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        View WaveWrap = View.inflate(this, R.layout.popup_audio, null);
        po = new PopupWindow(WaveWrap);
        po.setWidth(WRAP_CONTENT);
        po.setHeight(320);
        popupWave = WaveWrap.findViewById(R.id.wave);
        popupWave.setMode(0);
        popupWave.setWaves(all, true);

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                switch (newState) {
//                    case RecyclerView.SCROLL_STATE_IDLE:
//                        WaveView.pause = false;
//                        adapter.notifyDataSetChanged();
//                        break;
//                    case RecyclerView.SCROLL_STATE_SETTLING:
//                    case RecyclerView.SCROLL_STATE_DRAGGING:
//                        WaveView.pause = true;
//                        break;
//
//                }
//            }
//        });

    }
}
