package com.example.media_player.binds;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.media_player.services.MusicService;

public class turnOnPlaylistDelayAndLoud implements KeyBind {
    int[] keyCodeList;
    final String playlistName;
    Integer volume;
    int delay;

    public turnOnPlaylistDelayAndLoud(String playlistName, @Nullable Integer volume, int delay, int[] keycodelist) {
        this.playlistName = playlistName;
        this.volume = volume;
        this.delay = delay;
        this.keyCodeList = keycodelist;
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
//        try {
//            Thread.sleep(delay);//TODO
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    if (volume != null)
                        binder.setVolume(volume);
                    binder.setNewPlaylistAndPlay(playlistName);
            }
        }, delay*1000);
    }

    @Override
    public void removeBind() {

    }

    @Override
    public String getName() {
        if(volume != null)
            return "Включить плейлист "+playlistName+" через "+delay+" секунд с громкостью "+volume;
        else
            return "Включить плейлист "+playlistName+" через "+delay+" секунд";
    }

    @NonNull
    @Override
    public String toString() {
        return "Плейлисе "+playlistName+" будет включен с громкостью "+volume+" через "+delay +" секунд";
    }
}
