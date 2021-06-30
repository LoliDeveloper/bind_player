package com.example.media_player.myfragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.media_player.R;
import com.example.media_player.activities.BindingKeysActivity;
import com.example.media_player.binds.KeyBind;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static com.example.media_player.CommonFunctions.CreateKeyBindsForSerializationList;
import static com.example.media_player.CommonFunctions.resetBindingsInternal;


public class ShowAllBindsFragment extends Fragment implements ListAdapter.ItemClickListener {
    final String TAG = "Show All Binds Fragment";
    final String bindingFileName = "bindedKeys.txt";
    RecyclerView recyclerView;
    Button okay_showAllBinds;
    ArrayList<KeyBind> keyBindArrayList;

    public static ShowAllBindsFragment newInstance() {
        ShowAllBindsFragment fragment = new ShowAllBindsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_for_showallbinds, container, false);
        initRecyclerView(view);
        //        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview_showallbinds);
        okay_showAllBinds = (Button)view.findViewById(R.id.button_showallbinds);



        okay_showAllBinds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFragmentById(new EmptyFragment());
            }
        });
        return view;
    }
    private void initRecyclerView(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_showallbinds);
        keyBindArrayList = CreateKeyBindsForSerializationList(getContext(), TAG, bindingFileName);
        ListAdapter listAdapter = new ListAdapter(keyBindArrayList, this);
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

    }


    public void ShowFragmentById(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.pressKeyFrag, fragment);
        fragmentTransaction.commit();

    }


    @Override
    public void onItemClick(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Вы уверены?")
                .setMessage("Удалить \"" + keyBindArrayList.get(position).toString()+"\" ?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        keyBindArrayList.remove(position);
                        resetBindingsInternal(getContext(), TAG, bindingFileName, keyBindArrayList);
                        ((BindingKeysActivity)getActivity()).showAllBinds();
                    }
                })
                .setNegativeButton("Нет", null )
                .setIcon(R.drawable.ic_baseline_delete_forever_48)
                .show();

    }
}