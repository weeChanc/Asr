//package com.weechan.asr;
//
//import android.graphics.Path;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import java.util.List;
//
///**
// * @author 214652773@qq.com
// * @user c
// * @create 2018/12/30 21:27
// */
//
//public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private List<Record> records;
//
//    private static final int TYPE_TEXT = 1;
//    private static final int TYPE_RECORD = 2;
//
//    public MyAdapter(List<Record> records) {
//        super();
//        this.records = records;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
//        View view = null;
//        RecyclerView.ViewHolder holder = null ;
//        Log.e("MyAdapter", String.valueOf(type));
//        switch (type) {
//            case TYPE_TEXT: {
//                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_text,
//                        viewGroup, false);
//                holder = new TextViewHolder(view);
//                break;
//            }
//            case TYPE_RECORD: {
//                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_audio,
//                        viewGroup, false);
//                holder = new AudioViewHolder(view);
//                break;
//            }
//        }
//
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//        if(getItemViewType(viewHolder.getAdapterPosition()) == 1){
//            TextViewHolder holder = (TextViewHolder) viewHolder;
//            holder.textView.setText(records.get(i).getSentence());
//        }else{
//            AudioViewHolder holder = (AudioViewHolder) viewHolder;
//            Path cache = records.get(i).getCache();
//            if( cache == null) {
//                cache = holder.waveView.setWaves(records.get(i).getWaves(),true);
//            }
//            holder.waveView.setPath(cache);
//        }
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return records.size();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return records.get(position).getType();
//    }
//
//    class TextViewHolder extends RecyclerView.ViewHolder {
//
//        TextView textView ;
//        public TextViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textView = itemView.findViewById(R.id.text);
//        }
//    }
//
//    class AudioViewHolder extends RecyclerView.ViewHolder{
//        WaveView waveView;
//        public AudioViewHolder(@NonNull View itemView) {
//            super(itemView);
//            waveView = itemView.findViewById(R.id.wave);
//        }
//    }
//}
