package com.example.media_player.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.media_player.activities.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.List;

import static com.example.media_player.CommonFunctions.createPlaylistFiles;

//import static androidx.core.app.ActivityCompat.requestPermissions;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    EventObject trackChanged;
    private static final String TAG = "MUSIC SERVICE";
    final String defaultPlaylistFileName = "defaultPlaylist.txt";
    MediaPlayer player;
    List<Track> trackList;
    File[] playlist_files;
    File currentPlaylistFile;
    String currentTrackName;
    Playlist defaultPlaylist;
    String DIRECTORY_PATH;
    private int songPos = 0;
    boolean isBindLock = true;
    ArrayList<Integer> random_track_nums;
    int random_count = 0;
    private final IBinder msBinder = new MusicServiceBinder();
//    private Handler msHandler;
    private boolean msIsPaused = false;
    private boolean msIsStopped = true;
    final int randomConst = 2;
    MainActivity mainActivity;
    AudioManager audioManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return msBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playSong(randomConst);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        msIsPaused = false;
        msIsStopped = false;
    }


    public class MusicServiceBinder extends Binder implements Serializable {
        public MusicService getService(){
            return MusicService.this;
        }
        public void playDefaultPlaylist(){
            if(defaultPlaylist != null) {
                setNewPlaylistAndPlay(defaultPlaylist.name);
                setVolume(defaultPlaylist.volume);
            }
        }
        public void setMainActivity(MainActivity _mainActivity){
            mainActivity = _mainActivity;
        }
        public String getCurrentPlaylistName(){

            if(currentPlaylistFile == null){
                return "";
            }else{
                if(defaultPlaylist != null) {
                    if (currentPlaylistFile.getName().equals(defaultPlaylist.name)) {
                        return currentPlaylistFile.getName() + "\n(Плейлист по умолчанию)";
                    }
                }
                return currentPlaylistFile.getName();
            }
        }
        public String getCurrentTrackName(){
            if(currentPlaylistFile == null){
                return "";
            }else {
                return "("+(random_count) + "/" + trackList.size() + ") " + currentTrackName;
            }
        }
        public void setDefaultPlaylistFile(){
            Playlist readedPlaylist = readInternalDefaultPlaylist();
            if(readedPlaylist != null)
                defaultPlaylist = readedPlaylist;
        }
        public void setVolume(int volume){
            if(!isBindLock) {
                volume = (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * volume) / 100;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            }
        }
        public void changeVolume(int difference) {
            if (!isBindLock) {
                difference = (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * difference) / 100;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + difference, 0);
            }
        }
        public void startPlayer(){
            if(!isBindLock) {
                if(msIsStopped) {
                    if (playlist_files != null) {
                        if(defaultPlaylist != null) {
                            setNewPlaylistAndPlay(defaultPlaylist.name);
                        }else{
                            setNewPlaylistAndPlay(playlist_files[0].getName());
                        }
                    }
                }
                else player.start();
            }
        }
        public void nextPlay(){
            if(!isBindLock)
                playSong(randomConst);
        }
        public void pausePlayer(){
            if(!isBindLock) {
                player.pause();
                msIsPaused = true;
                msIsStopped = false;
            }
        }
        public void setNewPlaylistAndPlay(String playlistName){

            if(!isBindLock || (defaultPlaylist != null && playlistName.equals(defaultPlaylist.name))) {
                random_count = 0;
                populateTrackList(DIRECTORY_PATH, trackList, playlistName);
                random_track_nums = new ArrayList<>();
                for(int i = 0;i < trackList.size();++i){
                    random_track_nums.add(i);
                }
                Collections.shuffle(random_track_nums);
                playSong(randomConst);
            }
        }
        public boolean isPlayerStop(){
            return msIsStopped;
        }
        public void stopPlayer() {
            if (!isBindLock){
                player.stop();
                msIsStopped = true;
            }
        }
        public void setDirectory(String directory){
            DIRECTORY_PATH = directory;
            playlist_files = createPlaylistFiles(DIRECTORY_PATH);
            //populateTrackList(DIRECTORY_PATH, trackList, playlist_files[new Random().nextInt(playlist_files.length)].getName());
        }
        public boolean isPaused(){return  msIsPaused;}
//        public void setList(List<Track> theSongs){
//            trackList = theSongs;
//            random_count = 0;
//        }
        public void UnlockBinds(boolean notify_user){
            isBindLock = false;
            if(notify_user) {
                Toast.makeText(MusicService.this, "Binds unlocked", Toast.LENGTH_SHORT).show();
                if(mainActivity!=null){
                    mainActivity.setLockState();
                }
            }
        }
        public void LockBinds(boolean notify_user){
            isBindLock = true;
            if(notify_user) {
                Toast.makeText(MusicService.this, "Binds locked", Toast.LENGTH_SHORT).show();
                if (mainActivity != null) {
                    mainActivity.setLockState();
                }
            }
        }
        public boolean isLocked(){return isBindLock;}
    }

    private Playlist readInternalDefaultPlaylist() {
        Playlist playlist = null;
            try{
                FileInputStream fileInputStream
                        = openFileInput(defaultPlaylistFileName);
                ObjectInputStream objectInputStream
                        = new ObjectInputStream(fileInputStream);
                playlist = (Playlist) objectInputStream.readObject();
                objectInputStream.close();
            }catch(FileNotFoundException e){
                Log.e(TAG, e.getMessage());
            }catch (IOException e){
                Log.e(TAG, e.getMessage());
            }catch(ClassNotFoundException e){
                Log.e(TAG, e.getMessage());
            }

        return playlist;
    }

    private void populateTrackList(String directory_path, List<Track> trackList, String PlaylistName) {
        playlist_files = createPlaylistFiles(directory_path);
        if(playlist_files != null){
            Log.d(TAG, "PlaylistName = " + PlaylistName);
            for(File file:playlist_files){
                Log.d(TAG, "file.getName() = " + file.getName());
                if(file.getName().equals(PlaylistName)){
                    currentPlaylistFile = file;
                    break;
                }
            }
            if(currentPlaylistFile == null) {
                Toast.makeText(this, "File "+PlaylistName+" not found", Toast.LENGTH_SHORT).show();
                currentPlaylistFile = playlist_files[0];
                Log.e(TAG, "File "+PlaylistName+" not found");
            }
            File[] music = currentPlaylistFile.listFiles((dir, name) -> name.contains(".mp3")||name.contains(".wav"));
            if(music != null) {
                trackList.clear();
                for (File file : music) {
                    trackList.add(new Track(file.getName(), file.getAbsolutePath()));
                    Log.d(TAG, "Track " + file.getName() + " with path is " + file.getAbsolutePath() + " was added to music succesfully");
                }
                Collections.sort(trackList, new Comparator<Track>() {
                    @Override
                    public int compare(Track o1, Track o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                });
            }
        }else{
            Log.e(TAG,"playlist_files is null!");
        }
    }

    @Override
    public void onCreate() {super.onCreate();
        initMusicPlayer();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
//        msHandler = new Handler();
        msIsPaused = true;
        trackList = new ArrayList<>();
        random_track_nums = new ArrayList<>();
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    public void initMusicPlayer(){
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        player = new MediaPlayer();
        player.setWakeMode(getApplicationContext(),
            PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setLooping(true);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!msIsStopped) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }


    public void playSong(int psw){
        int i = 0;
        Log.d(TAG, "Try to reset");
        player.reset();
        Log.d(TAG, "Reset was succesful");
        if(DIRECTORY_PATH != null) {
            try {
                Log.d(TAG, "Files exists: " + new File(DIRECTORY_PATH).exists());
                if(trackList.size() < 1) player.setDataSource((MediaDataSource) null);
                else {
                    switch (psw) {
                        case randomConst:
                            if(random_count >= trackList.size()){
                                Collections.shuffle(random_track_nums);
                                random_count = 0;
                                if(defaultPlaylist != null){
                                    Log.e(TAG, "Заиграл плейлист по умолчнанию");
                                    Toast.makeText(getBaseContext(), "Заиграл плейлист по умолчнанию", Toast.LENGTH_SHORT).show();
                                    ((MusicServiceBinder)msBinder).playDefaultPlaylist();//IF defaultPlaylistFile != null SET IT!!!!!
                                    return;
                                }else{
                                    Log.e(TAG, "Плейлист по умолчанию не установлен");
                                    Toast.makeText(getBaseContext(), "Плейлист по умолчанию не установлен", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if(random_count < random_track_nums.size()) {
                                songPos = random_track_nums.get(random_count);
                                ++random_count;
                            }
                            break;
                    }
                }
                if(songPos < trackList.size()) {
                    currentTrackName = trackList.get(songPos).getTitle();
                    player.setDataSource(trackList.get(songPos).getTrackDirectory());
                }
                Log.d(TAG, "Data source was correct added: " + DIRECTORY_PATH + "/audio.mp3");
                player.setVolume(100,100);
                Log.d(TAG, "Volume was setted");
                if (mainActivity != null) mainActivity.getMusicServiceInfoForTexts();
                player.prepareAsync();
            } catch (IOException e) {
                Log.e(TAG, "Error setting data source: " + DIRECTORY_PATH + "/audio.mp3", e);
            }catch (NullPointerException e) {
                Log.e(TAG, "Files Null\ntrackList.size() = " + trackList.size(), e);
            }
        }else{
            //DIRECTORY_PATH = null
        }
    }

}

