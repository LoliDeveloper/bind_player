package com.example.media_player.myfragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.media_player.R;
import com.example.media_player.binds.KeyBind;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import static com.example.media_player.CommonFunctions.generateCoolString;

public class ListAdapter extends RecyclerView.Adapter {
    final String TAG = "List Adapter";
    final String bindingFileName = "bindedKeys.txt";
    ArrayList<KeyBind> keyBindArrayList;
    private ItemClickListener clickListener;

    ListAdapter(ArrayList<KeyBind> arrayListKeys, ItemClickListener clickListener){
        this.clickListener = clickListener;
        keyBindArrayList = arrayListKeys;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder Begin");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        Log.d(TAG, "onCreateViewHolder Return");
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder)holder).bindView(position);
        holder.itemView.findViewById(R.id.deleteImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keyBindArrayList.size();
    }
    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView titleTextView;
        private TextView bindButtonsTextView;
        private ImageButton deleteImageButton;

        public ListViewHolder(View itemView){
            super(itemView);
            titleTextView = (TextView)itemView.findViewById(R.id.text_title_binds);
            bindButtonsTextView = (TextView)itemView.findViewById(R.id.text_butons_binds);
            deleteImageButton = (ImageButton)itemView.findViewById(R.id.deleteImageButton);
            deleteImageButton.setOnClickListener(this);
        }

        public void bindView(int position){
            titleTextView.setText(keyBindArrayList.get(position).getName());
            String binds = generateCoolString(keyBindArrayList.get(position).getKeyCodes());
            bindButtonsTextView.setText(binds);
            deleteImageButton.setImageResource(R.drawable.ic_baseline_delete_forever_48);
        }


        @Override
        public void onClick(View v) {
        }
    }

    public interface ItemClickListener{
        public void onItemClick(int position);
    }
}
