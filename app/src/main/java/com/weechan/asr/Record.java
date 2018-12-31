package com.weechan.asr;

import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;

import java.util.List;

/**
 * @author 214652773@qq.com
 * @user c
 * @create 2018/12/30 21:49
 */

public class Record {
    private String sentence;
    private int type ;
    private List<Short> waves;
    private Path cache;

    public Path getCache() {
        return cache;
    }

    public void setCache(Path cache) {
        this.cache = cache;
    }

    public Record(String sentence, int type) {
        this.sentence = sentence;
        this.type = type;
    }

    public Record(int type) {
        this.type = type;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Short> getWaves() {
        return waves;
    }

    public void setWaves(List<Short> waves) {
        this.waves = waves;
    }
}
