package com.example.media_player.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.media_player.R;
import com.example.media_player.binds.AlphabeticNextTrackInPlaylistBind;
import com.example.media_player.binds.KeyBind;
import com.example.media_player.binds.LockKeyboardBind;
import com.example.media_player.binds.UnlockKeyboardBind;
import com.example.media_player.binds.turnOnPlaylistDelayAndLoud;
import com.example.media_player.myfragments.DialogBindVolume;
import com.example.media_player.myfragments.DialogBindingDelayPlaylist;
import com.example.media_player.myfragments.DialogForSetDefaultPlaylist;
import com.example.media_player.myfragments.DialogShortUnlock;
import com.example.media_player.myfragments.EmptyFragment;
import com.example.media_player.myfragments.PressKey;
import com.example.media_player.myfragments.ShowAllBindsFragment;
import com.example.media_player.services.AccessibilityKeyDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.example.media_player.CommonFunctions.CreateKeyBindsForSerializationList;
import static com.example.media_player.CommonFunctions.convertIntegers;
import static com.example.media_player.CommonFunctions.readInternal;
import static com.example.media_player.CommonFunctions.resetBindingsInternal;

public class BindingKeysActivity extends AppCompatActivity {

    final String bindingFileName = "bindedKeys.txt";
    final String TAG = "Binding Keys Activity";
    String DIRECTORY_PATH;
    Button delay_playlist_button;

    Button lock_button;
    Button unlock_button;
    Button short_unlock_button;
    Button set_volume_button;
    Button change_volume_button;
    Button next_track_bind_button;
    Button set_default_playlist_button;
    Button showAllBinds;

    ArrayList<KeyBind> keyBindsForSerializationList;
    int buttonResIdPressed = -1;

    HashMap<Integer, Boolean> bindHashMap;
    ArrayList<Integer> arrayBindCodes;
    TextView textFragmentBind;
    boolean isWaitForKey = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);
        setTitle("Binding");
        textFragmentBind = (TextView)findViewById(R.id.textFragmentBind);
        delay_playlist_button = (Button)findViewById(R.id.delay_playlist_button);
        lock_button = (Button)findViewById(R.id.lock_button);
        unlock_button = (Button)findViewById(R.id.unlock_button);
        short_unlock_button = (Button)findViewById(R.id.short_unlock_button);
        change_volume_button = (Button)findViewById(R.id.change_volume_button);
        set_volume_button = (Button)findViewById(R.id.set_volume_button);
        next_track_bind_button = (Button)findViewById(R.id.next_track_bind_button);
        set_default_playlist_button = (Button)findViewById(R.id.set_default_playlist_button);
        showAllBinds = (Button)findViewById(R.id.showAllBinds);
        arrayBindCodes = new ArrayList<>();
        updateKeyBinds();
        bindHashMap = new HashMap<>(30);
        if (DIRECTORY_PATH == null) {
            DIRECTORY_PATH = readInternal("data.txt", this) == null? null:readInternal("data.txt", this).split("\n")[0];
            if (DIRECTORY_PATH == null) {
                Toast.makeText(this, "Choose directory!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }
        delay_playlist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonResIdPressed = v.getId();
                isWaitForKey = true;
                ShowFragmentById(new PressKey());
            }
        });
        lock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonResIdPressed = v.getId();
                isWaitForKey = true;
                ShowFragmentById(new PressKey());
            }
        });
        unlock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonResIdPressed = v.getId();
                isWaitForKey = true;
                ShowFragmentById(new PressKey());
            }
        });
        short_unlock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonResIdPressed = v.getId();
                isWaitForKey = true;
                ShowFragmentById(new PressKey());
            }
        });
        set_volume_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonResIdPressed = v.getId();
                isWaitForKey = true;
                ShowFragmentById(new PressKey());
            }
        });
        change_volume_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonResIdPressed = v.getId();
                isWaitForKey = true;
                ShowFragmentById(new PressKey());
            }
        });
        next_track_bind_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonResIdPressed = v.getId();
                isWaitForKey = true;
                ShowFragmentById(new PressKey());
            }
        });
        set_default_playlist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogForSetDefaultPlaylist dialogForSetDefaultPlaylist = DialogForSetDefaultPlaylist.newInstance(DIRECTORY_PATH);
                dialogForSetDefaultPlaylist.show(getSupportFragmentManager(), "DialogForSetDefaultPlaylist");
            }
        });
        showAllBinds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonResIdPressed = v.getId();
                showAllBinds();
            }
        });
    }

    public void showAllBinds() {
        ShowAllBindsFragment showAllBindsFragment = ShowAllBindsFragment.newInstance();
        ShowFragmentById(showAllBindsFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void ShowFragmentById(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.pressKeyFrag, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        ArrayList<Integer> ArBindCodes = new ArrayList<>();
        ArBindCodes.addAll(arrayBindCodes);
        arrayBindCodes.clear();
        if (bindHashMap.get(keyCode) != null) {
            bindHashMap.put(keyCode, false);
        }
        if(isWaitForKey) {
            isWaitForKey = false;
            ShowFragmentById(new EmptyFragment());
            switch (buttonResIdPressed){
                case R.id.delay_playlist_button:
                    DialogBindingDelayPlaylist dialogBinding = DialogBindingDelayPlaylist.newInstance(DIRECTORY_PATH, convertIntegers(ArBindCodes));
                    dialogBinding.show(getSupportFragmentManager(), "DialogBindingDelayPlaylist");
                    break;
                case R.id.lock_button:
                    keyBindsForSerializationList = CreateKeyBindsForSerializationList(this, TAG, bindingFileName);
                    for (KeyBind keyBind:keyBindsForSerializationList){
                        if(Arrays.equals(keyBind.getKeyCodes(), convertIntegers(ArBindCodes))){
                            keyBindsForSerializationList.remove(keyBind);
                        }
                    }
                    keyBindsForSerializationList.add(new LockKeyboardBind(convertIntegers(ArBindCodes)));
                    resetBindingsInternal(this,TAG, bindingFileName, keyBindsForSerializationList);
                    break;

                case R.id.unlock_button:
                    keyBindsForSerializationList = CreateKeyBindsForSerializationList(this, TAG, bindingFileName);
                    Iterator<KeyBind> iter1 = keyBindsForSerializationList.iterator();
                    while (iter1.hasNext()){
                        if(Arrays.equals(iter1.next().getKeyCodes(), convertIntegers(ArBindCodes))){
                            iter1.remove();
                        }
                    }
                    keyBindsForSerializationList.add(new UnlockKeyboardBind(convertIntegers(ArBindCodes),-1));
                    resetBindingsInternal(this,TAG, bindingFileName, keyBindsForSerializationList);
                    break;
                case R.id.short_unlock_button:
                    DialogShortUnlock dialogShortUnlock = DialogShortUnlock.newInstance(convertIntegers(ArBindCodes));
                    dialogShortUnlock.show(getSupportFragmentManager(), "DialogShortUnlock");
                    break;
                case R.id.set_volume_button:
                    DialogBindVolume dialogSetVolume = DialogBindVolume.newInstance(convertIntegers(ArBindCodes), true);
                    dialogSetVolume.show(getSupportFragmentManager(), "DialogBindVolume");
                    break;
                case R.id.change_volume_button:
                    DialogBindVolume dialogChangeVolume = DialogBindVolume.newInstance(convertIntegers(ArBindCodes), false);
                    dialogChangeVolume.show(getSupportFragmentManager(), "DialogBindVolume");
                    break;
                case R.id.next_track_bind_button:
                    keyBindsForSerializationList = CreateKeyBindsForSerializationList(this, TAG, bindingFileName);
                    Iterator<KeyBind> iter2 = keyBindsForSerializationList.iterator();
                    while (iter2.hasNext()){
                        if(Arrays.equals(iter2.next().getKeyCodes(), convertIntegers(ArBindCodes))){
                            iter2.remove();
                        }
                    }
                    keyBindsForSerializationList.add(new AlphabeticNextTrackInPlaylistBind(convertIntegers(ArBindCodes)));
                    resetBindingsInternal(this,TAG, bindingFileName, keyBindsForSerializationList);
                    break;

            }
        }
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(bindHashMap.get(keyCode) == null || !(bindHashMap.get(keyCode))){
            bindHashMap.put(keyCode, true);
            arrayBindCodes.add(keyCode);
        }
        return super.onKeyDown(keyCode, event);
    }


    public void updateKeyBinds(){
        startService(new Intent(getApplicationContext(), AccessibilityKeyDetector.class));
    }

}
