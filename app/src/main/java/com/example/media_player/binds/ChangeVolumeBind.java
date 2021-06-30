package com.example.media_player.binds;

import androidx.annotation.NonNull;

import com.example.media_player.services.MusicService;

public class ChangeVolumeBind implements KeyBind {
    int[] keyCodeList;
    int volumeChange;

    public ChangeVolumeBind(int[] keyCodeList, int volumeChange) {
        this.keyCodeList = keyCodeList;
        this.volumeChange = volumeChange;
    }

    @Override
    public int[] getKeyCodes() {
        return keyCodeList;
    }

    @Override
    public int getKeyCount() {
        return keyCodeList.length;
    }

    @Override
    public void doTask(MusicService.MusicServiceBinder binder) {
        binder.changeVolume(volumeChange);
    }

    @Override
    public void removeBind() {

    }

    @Override
    public String getName() {
        return "Изменить громкость на "+volumeChange;
    }

    @NonNull
    @Override
    public String toString() {
        return "Изменить громкость на "+volumeChange;
    }
}
