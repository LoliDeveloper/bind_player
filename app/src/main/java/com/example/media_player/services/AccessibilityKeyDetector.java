package com.example.media_player.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.example.media_player.binds.KeyBind;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.media_player.CommonFunctions.convertIntegers;

public class AccessibilityKeyDetector extends AccessibilityService {
    private final String TAG = "AccessKeyDetector";
    MusicService.MusicServiceBinder binder;
    final String bindingFileName = "bindedKeys.txt";
    ArrayList<KeyBind> keyBindsForSerializationList;
    HashMap<Integer, Boolean> pressedKeysMap;
    ArrayList<Integer> pressedKeysList;

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        boolean ret = super.onKeyEvent(event);
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            Log.d(TAG,"Key pressed via accessibility is: "+event.getKeyCode());
            if(pressedKeysMap.get(event.getKeyCode()) == null || !pressedKeysMap.get(event.getKeyCode())) {
                pressedKeysMap.put(event.getKeyCode(), true);
                pressedKeysList.add(event.getKeyCode());
            }
        }else if(event.getAction() == KeyEvent.ACTION_UP){
            Log.d(TAG,"Key upped via accessibility is: "+event.getKeyCode());
            for(KeyBind keybind:keyBindsForSerializationList){
                int[] keyCodes = keybind.getKeyCodes();
                if(Arrays.equals(keyCodes, convertIntegers(pressedKeysList))){
                    if(binder != null) {
                        keybind.doTask(binder);
                    }
                    else
                    break;
                }
            }
            pressedKeysList.clear();
            pressedKeysMap.clear();

        }


        //This allows the key pressed to function normally after it has been used by your app.
        return ret;
    }


    @Override
    protected void onServiceConnected() {
        pressedKeysMap = new HashMap();
        pressedKeysList = new ArrayList<>();
        Log.i(TAG,"Service connected");
        Toast.makeText(this, "ACCESS SERVICE CONNECTED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(intent.getSerializableExtra("binder") != null){
            binder = (MusicService.MusicServiceBinder) intent.getSerializableExtra("binder");
            if(binder != null)
                binder.setDefaultPlaylistFile();
        }

        try{
            FileInputStream fileInputStream
                    = openFileInput(bindingFileName);
            ObjectInputStream objectInputStream
                    = new ObjectInputStream(fileInputStream);
            keyBindsForSerializationList = (ArrayList<KeyBind>) objectInputStream.readObject();
            objectInputStream.close();
        }catch(FileNotFoundException e){
            Log.e(TAG, e.getMessage());
        }catch (IOException e){
            Log.e(TAG, e.getMessage());
        }catch(ClassNotFoundException e){
            Log.e(TAG, e.getMessage());
        }
        if(keyBindsForSerializationList == null)keyBindsForSerializationList = new ArrayList<>();

        return START_STICKY;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }


    @Override
    public void onInterrupt() {
    }
}
