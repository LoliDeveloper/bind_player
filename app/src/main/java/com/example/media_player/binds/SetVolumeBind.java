package com.example.media_player.binds;

import androidx.annotation.NonNull;

import com.example.media_player.services.MusicService;

public class SetVolumeBind implements KeyBind{

    int[] keyCodes;
    int newVolumeValue;

    public SetVolumeBind(int[] keyCodes, int newVolumeValue) {
        this.keyCodes = keyCodes;
        this.newVolumeValue = newVolumeValue;
    }

    @Override
    public int[] getKeyCodes() {
        return keyCodes;
    }

    @Override
    public int getKeyCount() {
        return keyCodes.length;
    }

    @Override
    public void doTask(MusicService.MusicServiceBinder binder) {
        binder.setVolume(newVolumeValue);
    }

    @Override
    public void removeBind() {

    }

    @Override
    public String getName() {
        return "Установить громкость на "+newVolumeValue+"%";
    }

    @NonNull
    @Override
    public String toString() {
        return "Установить громкость на " + newVolumeValue;
    }
}
