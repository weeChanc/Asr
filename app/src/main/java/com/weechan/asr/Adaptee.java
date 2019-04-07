//package com.weechan.asr;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import okhttp3.FormBody;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//
//import android.annotation.SuppressLint;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.weechan.asr.data.SoundSource;
//import com.weechan.asr.net.OkhttpKt;
//import com.weechan.asr.utils.AudioRecorder;
//import com.weechan.asr.utils.MusicPlayer;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.List;
//
//import static android.view.MotionEvent.ACTION_UP;
//
//public class Adaptee extends RecyclerView.Adapter<Adaptee.SoundHolder> {
//
//    List<SoundSource> records;
//
//    private int checkIndex = 0;
//
//    public Adaptee(List<SoundSource> records) {
//        this.records = records;
//    }
//
////    public void addWavesInActivePos(List<Short> waves){
////        sources.get(checkIndex).setWaves(waves);
////        notifyItemChanged(checkIndex);
////    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    @NonNull
//    @Override
//    public SoundHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view;
//        if (i == 1) {
//            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_new, viewGroup, false);
//        } else {
//            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pre, viewGroup, false);
//        }
//
//        SoundHolder holder = new SoundHolder(view, i);
//
//        if (holder.play != null) {
//            holder.play.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    MusicPlayer.Companion.play(records.get(holder.getAdapterPosition()).getWav());
//                }
//            });
//
//            holder.record.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            AudioRecorder.getInstant().startRecord(new AudioRecorder.Listener() {
//                                @Override
//                                public void onDataAvaliable(byte[] data) {
//                                }
//
//                                @Override
//                                public void onPause() {
//                                }
//
//                                @Override
//                                public void onStop() {
//                                }
//                            });
//                            break;
//                        case ACTION_UP:
//                            List<byte[]> bytes = AudioRecorder.getInstant().stop();
//                            ByteArrayOutputStream os = new ByteArrayOutputStream();
//                            for (byte[] aByte : bytes) {
//                                try {
//                                    os.write(aByte);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            AudioRecorder.convertPcmToWav(os.toByteArray(),16000,2,16);
//
//                            Request req =
//                                    new Request.Builder()
//                                    .url("127.0.0.1:5000/calculate")
//                                    .post(RequestBody.create(MediaType.parse(),))
//                            OkhttpKt.getOkClient().newCall()
//                    }
//
//                    return true;
//                }
//            });
//        }
//
//        view.setOnClickListener(v -> {
//            if (checkIndex != holder.getAdapterPosition()) {
//                notifyItemChanged(checkIndex, null);
//                checkIndex = holder.getAdapterPosition();
//                notifyItemChanged(checkIndex, null);
//            }
//        });
//
//        return holder;
//    }
//
//
//    @Override
//    public void onBindViewHolder(@NonNull SoundHolder holder, int i) {
//        switch (getItemViewType(holder.getAdapterPosition())) {
//            case TYPE_NEW: {
//                holder.page.setText(holder.getAdapterPosition() + 1 + "/" + records.size());
//            }
//            case TYPE_PRE: {
//
//            }
//        }
//        holder.title.setText(records.get(i).getSentence().getContent());
//    }
//
//    public final static int TYPE_PRE = 0;
//    public final static int TYPE_NEW = 1;
//
//    @Override
//    public int getItemViewType(int position) {
//        return position == checkIndex ? 1 : 0;
//    }
//
//    @Override
//    public int getItemCount() {
//        return records.size();
//    }
//
//    class SoundHolder extends RecyclerView.ViewHolder {
//
//        TextView title;
//
//        ImageView play;
//        ImageView record;
//        TextView page;
//
//        public SoundHolder(@NonNull View itemView, int type) {
//            super(itemView);
//            if (type == TYPE_PRE) {
//                title = itemView.findViewById(R.id.title);
//            } else {
//                title = itemView.findViewById(R.id.spannable);
//                play = itemView.findViewById(R.id.play);
//                record = itemView.findViewById(R.id.record);
//                page = itemView.findViewById(R.id.page);
//            }
//        }
//    }
//}
