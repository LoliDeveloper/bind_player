package com.example.media_player.myfragments;
/*
* Диалог для создания бинда с задержкой и громкостью
*
* */

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.media_player.activities.BindingKeysActivity;
import com.example.media_player.R;
import com.example.media_player.binds.KeyBind;
import com.example.media_player.binds.turnOnPlaylistDelayAndLoud;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.media_player.CommonFunctions.CreateKeyBindsForSerializationList;
import static com.example.media_player.CommonFunctions.createPlaylistFiles;
import static com.example.media_player.CommonFunctions.generateCoolString;
import static com.example.media_player.CommonFunctions.removeSimilarBinds;
import static com.example.media_player.CommonFunctions.resetBindingsInternal;

public class DialogBindingDelayPlaylist extends DialogFragment {
    private static final String TAG = "DialogBinding";

    //widgets
    final String bindingFileName = "bindedKeys.txt";
    private EditText delayInput;
    private EditText volumeInput;
    private TextView mActionOk, mActionCancel;
    private Spinner spinner;
    int[] keyCodes;
    ArrayList<KeyBind> keyBindsForSerializationList;
    String DIRECTORY_PATH;

    //vars
    public static DialogBindingDelayPlaylist newInstance(String DIRECTORY_PATH, int[] keyCodeArgs) {
        DialogBindingDelayPlaylist f = new DialogBindingDelayPlaylist();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("DIRECTORY_PATH", DIRECTORY_PATH);
        args.putIntArray("keyCodeArgs", keyCodeArgs);
        f.setArguments(args);
        return f;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_for_create_delayplaylist_bind, container, false);
        mActionCancel = view.findViewById(R.id.action_cancel);
        mActionOk = view.findViewById(R.id.action_ok);
        delayInput = view.findViewById(R.id.delayInput);
        volumeInput = view.findViewById(R.id.volumeInput);
        spinner = view.findViewById(R.id.directory_spinner);
        keyBindsForSerializationList = CreateKeyBindsForSerializationList(getContext(), TAG, bindingFileName);

        Bundle args = getArguments();
        DIRECTORY_PATH = args.getString("DIRECTORY_PATH", null);
        keyCodes = args.getIntArray("keyCodeArgs");
        File[] playlistFiles = createPlaylistFiles(DIRECTORY_PATH);
        String[] arraySpinner = new String[playlistFiles.length];
        for(int i = 0; i < playlistFiles.length; ++i){
            arraySpinner[i] = playlistFiles[i].getName();
        }
        if(playlistFiles.length == 0){
            Toast.makeText(getContext(), "НЕТ ПАПОК С МУЗЫКОЙ", Toast.LENGTH_SHORT).show();
            this.dismiss();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String delayInputString = delayInput.getText().toString();
                String volumeInputString = volumeInput.getText().toString();
                int delay = 0;
                int volume = 100;
                if(!delayInputString.equals("")){
                    delay = Integer.parseInt(delayInputString);
                }
                if(!volumeInputString.equals("")){
                    volume = Integer.parseInt(volumeInputString);
                }
                String s = generateCoolString(keyCodes);
                Toast.makeText(getContext(), "Бинд установлен для кнопки " + s, Toast.LENGTH_SHORT).show();

                if(removeSimilarBinds(keyBindsForSerializationList, keyCodes)) {
                    keyBindsForSerializationList.add(new turnOnPlaylistDelayAndLoud(spinner.getSelectedItem().toString(), volume, delay, keyCodes));

                    resetBindingsInternal(getContext(), TAG, bindingFileName, keyBindsForSerializationList);
                }
                    ((BindingKeysActivity) getActivity()).updateKeyBinds();
                    getDialog().dismiss();
            }
        });
        return view;
    }



}
