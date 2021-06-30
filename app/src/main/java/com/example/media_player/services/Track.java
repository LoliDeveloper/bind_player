package com.example.media_player.services;

public class Track {

    private  String title;
    private  String trackDirectory;

    public Track(String title, String trackDirectory){
        this.title = title;
        this.trackDirectory = trackDirectory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTrackDirectory() {
        return trackDirectory;
    }

    public void setTrackDirectory(String trackDirectory) {
        this.trackDirectory = trackDirectory;
    }
}
