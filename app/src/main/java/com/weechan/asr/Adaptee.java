package com.weechan.asr;

import android.graphics.Path;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.weechan.asr.widget.TextBoard;
import com.weechan.asr.widget.WaveView;

import java.util.List;

public class Adaptee extends RecyclerView.Adapter<Adaptee.SoundHolder> {

    List<Record> records;

    private int checkIndex;

    public Adaptee(List<Record> records) {
        this.records = records;
    }

    public void addWavesInActivePos(List<Short> waves){
        records.get(checkIndex).setWaves(waves);
        notifyItemChanged(checkIndex);
    }

    @NonNull
    @Override
    public SoundHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sound,viewGroup,false);
        SoundHolder holder = new SoundHolder(view);
        view.setOnClickListener(v -> {
            notifyItemChanged(checkIndex,null);
            checkIndex = holder.getAdapterPosition();
            notifyItemChanged(checkIndex,null);
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SoundHolder holder, int i) {
        System.out.println("cc");
        Path cache = records.get(i).getCache();
        if( cache == null) {
            cache = holder.waveView.setWaves(records.get(i).getWaves(),true);
        }
        System.out.println(records.get(i).getWaves());
        holder.sentence.setText(records.get(i).getPhn().getText());
        holder.waveView.setPath(cache);
        holder.real.setTexts(records.get(i).getPhn().getSpell());
        holder.waveView.postInvalidate();
        if(holder.getAdapterPosition() == checkIndex){
            holder.check.setChecked(true);
        }else{
            holder.check.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    class SoundHolder extends RecyclerView.ViewHolder{

        WaveView waveView;
        TextView sentence;
        TextBoard real;
        TextBoard recognized;
        RadioButton check;

        public SoundHolder(@NonNull View itemView) {
            super(itemView);
            waveView = itemView.findViewById(R.id.wave);
            sentence = itemView.findViewById(R.id.text_sentence);
            real = itemView.findViewById(R.id.text_real);
            recognized = itemView.findViewById(R.id.text_recognized);
            check = itemView.findViewById(R.id.checkbox);
        }
    }
}
