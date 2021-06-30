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
import com.example.media_player.binds.KeyBind;
import com.example.media_player.binds.UnlockKeyboardBind;
import com.example.media_player.binds.turnOnPlaylistDelayAndLoud;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.media_player.CommonFunctions.CreateKeyBindsForSerializationList;
import static com.example.media_player.CommonFunctions.generateCoolString;
import static com.example.media_player.CommonFunctions.removeSimilarBinds;
import static com.example.media_player.CommonFunctions.resetBindingsInternal;

public class DialogShortUnlock extends DialogFragment {
    final String TAG = "Dialog Short Unlock";

    final String bindingFileName = "bindedKeys.txt";
    private EditText input_shortUnlock;
    private TextView action_ok_shortUnlock, action_cancel_shortUnlock;
    int[] keyCodes;
    ArrayList<KeyBind> keyBindsForSerializationList;

    public static DialogShortUnlock newInstance(int[] keyCodeArgs) {
        DialogShortUnlock f = new DialogShortUnlock();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putIntArray("keyCodeArgs", keyCodeArgs);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_for_short_unlock, container, false);

        action_cancel_shortUnlock = view.findViewById(R.id.action_cancel_shortUnlock);
        action_ok_shortUnlock = view.findViewById(R.id.action_ok_shortUnlock);
        input_shortUnlock = view.findViewById(R.id.input_shortUnlock);

        keyBindsForSerializationList = CreateKeyBindsForSerializationList(getContext(), TAG, bindingFileName);
        Bundle args = getArguments();
        keyCodes = args.getIntArray("keyCodeArgs");
        action_cancel_shortUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        action_ok_shortUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = input_shortUnlock.getText().toString();
                int duration = Integer.parseInt(input);

                String s = generateCoolString(keyCodes);
                if(s.equals("")){
                    Toast.makeText(getContext(), "Не удалось зафиксировать кнопку нажатия", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Бинд установлен для кнопки " + s, Toast.LENGTH_SHORT).show();

                    removeSimilarBinds(keyBindsForSerializationList, keyCodes);
                    keyBindsForSerializationList.add(new UnlockKeyboardBind(keyCodes, duration));
                    resetBindingsInternal(getContext(), TAG, bindingFileName, keyBindsForSerializationList);

                }
                ((BindingKeysActivity) getActivity()).updateKeyBinds();
                getDialog().dismiss();
            }
        });

        return view;
    }
}
