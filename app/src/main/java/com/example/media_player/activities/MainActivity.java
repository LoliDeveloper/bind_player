package com.example.media_player.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.media_player.CommonFunctions;
import com.example.media_player.R;
import com.example.media_player.services.AccessibilityKeyDetector;
import com.example.media_player.services.MusicService;
import com.example.media_player.services.Restarter;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import static com.example.media_player.CommonFunctions.checkAccessibilityPermission;
import static com.example.media_player.CommonFunctions.readInternal;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    String DIRECTORY_PATH;

    ImageView lock_state_image;

    ImageButton play;
    ImageButton pause;
    ImageButton nextPlay;
    TextView title;
    MusicService.MusicServiceBinder binder;

    TextView playlist_text;
    TextView title_track_text;

    Intent musicIntent;
    MusicService mService;
    private boolean musicBound = false;


    NotificationManager notificationManager;

    private  ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MusicService.MusicServiceBinder) service;
            Intent intentAB = new Intent(getApplicationContext(), AccessibilityKeyDetector.class);
            intentAB.putExtra("binder", (Serializable) binder);
            startService(intentAB);
            setBinderMainActivity();
            mService = binder.getService();
            Log.d(TAG, "DIRECTORY PATH = "+DIRECTORY_PATH);
            if(DIRECTORY_PATH != null) {
                Log.d(TAG, "DIRECTORY PATH exists: " + new File(DIRECTORY_PATH).exists());
                binder.setDirectory(DIRECTORY_PATH);
            if(binder.isPlayerStop()) {
                boolean state = binder.isLocked();
                if(state)binder.UnlockBinds(false);
                binder.startPlayer();
                if(state)binder.LockBinds(false);
                }
            }else{

            }
            musicBound = true;
            playlist_text.setText(binder.getCurrentPlaylistName());
            title_track_text.setText(binder.getCurrentTrackName());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    void setBinderMainActivity(){
        binder.setMainActivity(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Audio Player");
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        nextPlay = findViewById(R.id.nextPlay);
        playlist_text = findViewById(R.id.playlist_text);
        title_track_text = findViewById(R.id.title_track_text);
        lock_state_image = findViewById(R.id.lock_state_image);
        if(binder != null) {
            setLockState();
            getMusicServiceInfoForTexts();
        }
    }
    private boolean isMusicServiceStarted() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : list) {
            if (MusicService.class.getName().equals(service.service.getClassName())) {
                Log.i (TAG, "Service Running");
                bindService();
                return true;
            }
        }
        Log.i (TAG, "Service not running");
        return false;
    }

    @Override
    protected void onStart() {
        if(binder != null) {
            setLockState();
            getMusicServiceInfoForTexts();
        }
        if(DIRECTORY_PATH == null){
            CommonFunctions.CheckReadPermission(this, this);
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(binder != null) {
            setLockState();
            getMusicServiceInfoForTexts();
            binder.setDefaultPlaylistFile();
        }
        if (DIRECTORY_PATH == null) {
            DIRECTORY_PATH = readInternal("data.txt", this) == null? null:readInternal("data.txt", this).split("\n")[0];
            if (DIRECTORY_PATH == null) {
                startActivity(new Intent(this, SettingActivity.class));
                return;
            }
        }
        //bindService();

    }
    private void bindService(){
        Intent serviceIntent = new Intent(this, MusicService.class);
        boolean success = bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        if(!success){
//            success = bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            Toast.makeText(this, "Не могу создать сервис!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if(binder != null) {
            binder.setMainActivity(null);
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

//    private void createChanenel() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
//                    "KOD Dev", NotificationManager.IMPORTANCE_LOW);
//
//            notificationManager = getSystemService(NotificationManager.class);
//            if(notificationManager != null){
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2909: {
                boolean AB = checkAccessibilityPermission(this);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isMusicServiceStarted()) {
                        musicIntent = new Intent(this, MusicService.class);
                        bindService();
                        startService(musicIntent);
                    }

                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean state = binder.isLocked();
                            if(state)binder.UnlockBinds(false);
                            binder.startPlayer();
                            if(state)binder.LockBinds(false);
                        }
                    });
                    pause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean state = binder.isLocked();
                            if(state)binder.UnlockBinds(false);
                            binder.pausePlayer();
                            if(state)binder.LockBinds(false);
                        }
                    });
                    nextPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean state = binder.isLocked();
                            if(state)binder.UnlockBinds(false);
                            binder.nextPlay();
                            if(state)binder.LockBinds(false);
                        }
                    });

                    DIRECTORY_PATH = readInternal("data.txt", this) == null ? null : readInternal("data.txt", this).split("\n")[0];
                    if (DIRECTORY_PATH == null) {
                        Toast.makeText(this, "Сначала укажите папку с папками", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, SettingActivity.class));
                    } else {
                        Log.d(TAG, "File( lenght = " + DIRECTORY_PATH.split("\n").length + ").exists() is: " + new File(DIRECTORY_PATH.split("\n")[0]).exists());
                        if(binder != null){
                            binder.setDirectory(DIRECTORY_PATH);
                        }
                    }


                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }
    public void setLockState(){
        if(binder.isLocked()){ // return isBindLocked
            lock_state_image.setImageResource(R.drawable.ic_baseline_lock_48);
        }else{
            lock_state_image.setImageResource(R.drawable.ic_baseline_lock_open_48);
        }
    }
    public void getMusicServiceInfoForTexts() {
        playlist_text.setText(binder.getCurrentPlaylistName());
        title_track_text.setText(binder.getCurrentTrackName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_chose_directory_button:
                return true;

            case R.id.action_setting_button:
                startActivity(new Intent(this, SettingActivity.class));
                return true;

            case R.id.action_about_button:
                startActivity(new Intent(this, AboutActivity.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }






    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG,"Key pressed");

//this prevents the key from performing the base function. Replace with super.onKeyDown to let it perform it's original function, after being consumed by your app.
        return super.onKeyDown(keyCode, event);

    }
}
