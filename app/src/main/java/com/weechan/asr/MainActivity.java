//package com.weechan.asr;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import androidx.core.app.ActivityCompat;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import kotlin.Unit;
//import kotlin.jvm.functions.Function0;
//
//import android.view.Gravity;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.PopupWindow;
//
//import com.weechan.asr.utils.permission.Permission;
//import com.weechan.asr.utils.permission.PermissionCompatActivity;
//import com.weechan.asr.utils.permission.PermissionMan;
//import com.weechan.asr.widget.WaveView;
//
//import java.io.File;
//
//import static android.view.MotionEvent.ACTION_UP;
//
//public class MainActivity extends PermissionCompatActivity {
//
//    private RecyclerView recyclerView;
//    private Adaptee adapter;
//
//    private FloatingActionButton fab;
//
//    SoundModel model = new SoundModel();
//    private PopupWindow po;
//    private WaveView popupWave;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Permission.STORAGE.doAfterGet(this, () -> {
//            Permission.AUDIO.doAfterGet(MainActivity.this,()->{
//
//            });
//            return null;
//        });
//        initView();
//    }
//
//    private void startRecord() {
//        po.showAtLocation(findViewById(R.id.container), Gravity.CENTER, 0, 0);
//        model.start(() -> popupWave.setWaves(model.getDatas(),true));
//    }
//
//    private void stopRecord() {
//        adapter.addWavesInActivePos(model.getDatas());
//        adapter.notifyDataSetChanged();
//        model.stop();
//        popupWave.setWaves(null,true);
//        po.dismiss();
//
//
//
//
////
////        new Thread(() -> {
////            long time = System.currentTimeMillis();
////            runOnUiThread(() -> {
////                Toast.makeText(this, "开始分 析,分析未完成请不要操作", Toast.LENGTH_SHORT).show();
////            });
////            File outFile = new File(curFile.getAbsolutePath() + ".wav");
////            AudioRecorder.convertPcmToWav(curFile.getAbsolutePath(), outFile.getAbsolutePath(), 16000, 1,   16);
////            Analyze.analyze(Environment.getExternalStorageDirectory().getPath() + "/SA2_.wav");
////            runOnUiThread(() -> {
////                Toast.makeText(this, "分析耗时　" + (System.currentTimeMillis() - time), Toast.LENGTH_SHORT).show();
////                StringBuilder result = new StringBuilder();
////                try {
////                    FileInputStream fis = new FileInputStream(new File(getFilesDir()+"/model", "output.txt"));
////                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
////                    String line;
////                    while ((line = br.readLine()) != null) {
////                        String[] two = line.split(",");
////                        int soundIndex = Integer.parseInt(two[0]);
////                        long pos = Long.parseLong(two[1]);
////                        String sound = soundMap.get(soundIndex);
////                        result.append(sound);
////                    }
////                } catch (FileNotFoundException e) {
////                    e.printStackTrace();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////                records.add(new Record(result.toString(), 1));
////                popupWave.setWaves(null, false);
////                adapter.notifyDataSetChanged();
////            });
////        }).start();
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void initView() {
//
//        adapter = new Adaptee(model.records);
//        fab = findViewById(R.id.fab);
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
//        recyclerView.setItemAnimator(null);
//
//        View WaveWrap = View.inflate(this, R.layout.popup_audio, null);
//        po = new PopupWindow(WaveWrap);
//        po.setWidth(dp2px(240));
//        po.setHeight(dp2px(120));
//        popupWave = WaveWrap.findViewById(R.id.wave);
//        popupWave.setMode(0);
//        popupWave.setWaves(model.getDatas(), true);
//
//        fab.setOnTouchListener((v, event) -> {
//            int action = event.getAction();
//            if (action == MotionEvent.ACTION_DOWN) {
//                startRecord();
//            }
//
//            if (action == ACTION_UP) {
//                stopRecord();
//            }
//
//            return true;
//        });
//
//    }
//
//    private int dp2px(final float dpValue) {
//        final float scale = getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
//    }
//}
