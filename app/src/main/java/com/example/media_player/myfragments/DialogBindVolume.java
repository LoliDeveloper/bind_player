package com.example.media_player.myfragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.media_player.R;
import com.example.media_player.activities.BindingKeysActivity;
import com.example.media_player.binds.ChangeVolumeBind;
import com.example.media_player.binds.KeyBind;
import com.example.media_player.binds.SetVolumeBind;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.media_player.CommonFunctions.CreateKeyBindsForSerializationList;
import static com.example.media_player.CommonFunctions.generateCoolString;
import static com.example.media_player.CommonFunctions.removeSimilarBinds;
import static com.example.media_player.CommonFunctions.resetBindingsInternal;

public class DialogBindVolume extends DialogFragment {
    private static final String TAG = "Dialog Bind Volume";

    //widgets
    final String bindingFileName = "bindedKeys.txt";
    private EditText input_volume;
    private TextView action_ok_volume, action_cancel_volume;

    int[] keyCodes;
    ArrayList<KeyBind> keyBindsForSerializationList;
    boolean trueIfSetValue;

    public static DialogBindVolume newInstance(int[] keyCodeArgs, boolean trueIfSetValue) {
        DialogBindVolume f = new DialogBindVolume();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putIntArray("keyCodeArgs", keyCodeArgs);
        args.putBoolean("trueIfSetValue", trueIfSetValue);
        f.setArguments(args);
        return f;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_volume, container, false);

        action_cancel_volume = view.findViewById(R.id.action_cancel_volume);
        action_ok_volume = view.findViewById(R.id.action_ok_volume);
        input_volume = view.findViewById(R.id.input_volume);

        keyBindsForSerializationList = CreateKeyBindsForSerializationList(getContext(), TAG, bindingFileName);
        Bundle args = getArguments();
        keyCodes = args.getIntArray("keyCodeArgs");
        trueIfSetValue = args.getBoolean("trueIfSetValue");
        action_cancel_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        action_ok_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = input_volume.getText().toString();
                int newValue = Integer.parseInt(input);
                removeSimilarBinds(keyBindsForSerializationList, keyCodes);

                if(trueIfSetValue)
                    keyBindsForSerializationList.add(new SetVolumeBind(keyCodes, newValue));
                else
                    keyBindsForSerializationList.add(new ChangeVolumeBind(keyCodes, newValue));

                String s = generateCoolString(keyCodes);
                if(s.equals("")){
                    Toast.makeText(getContext(), "Не удалось зафиксировать кнопку нажатия", Toast.LENGTH_SHORT).show();
                }else {
                    resetBindingsInternal(getContext(), TAG, bindingFileName, keyBindsForSerializationList);
                    ((BindingKeysActivity) getActivity()).updateKeyBinds();
                }
                getDialog().dismiss();
            }
        });

        return view;
    }



}
