package com.weechan.asr;

import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.util.ArrayMap;

import java.util.List;

/**
 * @author 214652773@qq.com
 * @user c
 * @create 2018/12/30 21:49
 */

public class Record {

    private List<Short> waves;
    private Path cache;
    private PHN phn;

    public PHN getPhn() {
        return phn;
    }

    public void setPhn(PHN phn) {
        this.phn = phn;
    }

    public Path getCache() {
        return cache;
    }

    public void setCache(Path cache) {
        this.cache = cache;
    }


    public List<Short> getWaves() {
        return waves;
    }

    public void setWaves(List<Short> waves) {
        this.waves = waves;
    }
}
