package com.example.media_player.myfragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

import com.example.media_player.R;
import com.example.media_player.activities.BindingKeysActivity;
import com.example.media_player.binds.KeyBind;
import com.example.media_player.services.Playlist;

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

import static com.example.media_player.CommonFunctions.createPlaylistFiles;

public class DialogForSetDefaultPlaylist extends DialogFragment {
    private static final String TAG = "DialogBinding";

    //widgets
    final String defaultPlaylistFileName = "defaultPlaylist.txt";
    private EditText mInput;
    private TextView mActionOk, mActionCancel;
    private Spinner spinner;
    String DIRECTORY_PATH;

    public static DialogForSetDefaultPlaylist newInstance(String DIRECTORY_PATH) {
        DialogForSetDefaultPlaylist f = new DialogForSetDefaultPlaylist();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("DIRECTORY_PATH", DIRECTORY_PATH);
        f.setArguments(args);
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_for_default_playlist, container, false);
        mActionCancel = view.findViewById(R.id.action_cancel_default_playlist);
        mActionOk = view.findViewById(R.id.action_ok_default_playlist);
        mInput = view.findViewById(R.id.input_default_playlist);
        spinner = view.findViewById(R.id.directory_spinner_default_playlist);

        Bundle args = getArguments();
        DIRECTORY_PATH = args.getString("DIRECTORY_PATH", null);
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
                String input = mInput.getText().toString();
                int volume = 0;
                if(!input.equals("")){
                    volume = Integer.parseInt(input);
                }



                savePlaylistInternal(new Playlist(spinner.getSelectedItem().toString(), volume, new File(DIRECTORY_PATH+"/"+spinner.getSelectedItem().toString())));
                ((BindingKeysActivity)getActivity()).updateKeyBinds();
                getDialog().dismiss();
            }
        });
        return view;
    }

    private void savePlaylistInternal(Playlist playlist) {
        try {

            FileOutputStream fileOutputStream
                    = getContext().openFileOutput(defaultPlaylistFileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(playlist);
            objectOutputStream.flush();
            objectOutputStream.close();
        }catch(FileNotFoundException e){
            Log.e(TAG, e.getMessage());
        }catch (IOException e){
            Log.e(TAG, e.getMessage());
        }catch (NullPointerException e){
            Log.e(TAG, e.getMessage());
        }
    }


}
