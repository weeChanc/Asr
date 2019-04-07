package com.weechan.asr.data;

import android.graphics.Path;

import com.weechan.asr.data.PHN;

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
