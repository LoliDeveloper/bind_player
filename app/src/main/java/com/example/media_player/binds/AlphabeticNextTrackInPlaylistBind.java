package com.example.media_player.binds;

import com.example.media_player.services.MusicService;

public class AlphabeticNextTrackInPlaylistBind implements KeyBind{
    int[] keyCodes;

    public AlphabeticNextTrackInPlaylistBind(int[] keyCodes) {
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
        binder.nextPlay();
    }

    @Override
    public void removeBind() {

    }

    @Override
    public String getName() {
        return "Следующий трек в текущем плейлисте";
    }
}
