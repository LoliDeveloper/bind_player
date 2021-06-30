package com.example.media_player.services;

import java.io.File;
import java.io.Serializable;

public class Playlist implements Serializable {
    public Playlist(String name, int volume, File playlistFile) {
        this.name = name;
        this.volume = volume;
        this.playlistFile = playlistFile;
    }

    public String name;
    public int volume;
    public File playlistFile;
}
