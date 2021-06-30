package com.example.media_player.binds;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.media_player.services.MusicService;

public class LockKeyboardBind implements KeyBind {
    int[] keyCodes;

    public LockKeyboardBind(int[] keyCodes) {
        this.keyCodes = keyCodes;
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
        binder.LockBinds(true);
    }

    @Override
    public void removeBind() {

    }

    @Override
    public String getName() {
        return "Блокировка";
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
