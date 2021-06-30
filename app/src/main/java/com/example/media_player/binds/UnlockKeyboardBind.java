package com.example.media_player.binds;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.media_player.services.MusicService;

public class UnlockKeyboardBind implements KeyBind {
    int[] keyCodes;
    int during;

    public UnlockKeyboardBind(int[] keyCodes, int during) {
        this.keyCodes = keyCodes;
        this.during = during;
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
        binder.UnlockBinds(true);
        if(during>0){
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binder.LockBinds(true);
                }
            }, during*1000);
        }
    }

    @Override
    public void removeBind() {}

    @Override
    public String getName() {
        if(during>0) return "Разблокировать бинды на "+during+" секунд";
        else return "Разблокировать бинды";
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
