package com.example.media_player.binds;

import com.example.media_player.services.MusicService;

import java.io.Serializable;

public interface KeyBind extends Serializable {
    int[] getKeyCodes();
    int getKeyCount();
    void doTask(MusicService.MusicServiceBinder binder);
    void removeBind();
    String getName();

}
