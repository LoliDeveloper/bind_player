package com.example.media_player.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.media_player.R;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.example.media_player.CommonFunctions.CheckReadPermission;
import static com.example.media_player.CommonFunctions.readInternal;
import static com.example.media_player.CommonFunctions.saveInternal;


public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "Setting Activity";
    TextView pathText;
    TextView keyText;
    Button directoryButton;
    Button keyBindsButton;
    String PATH_DIRECTORY;
    DirectoryChooserConfig.Builder builder;
    Intent chooserIntent;
    Intent bindingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("Настройки");
        String initialDirectory = System.getenv("SECONDARY_STORAGE");
        if ((null == initialDirectory) || (initialDirectory.length() == 0)) {
            initialDirectory = System.getenv("EXTERNAL_SDCARD_STORAGE");
        }
        if ((null == initialDirectory) || (initialDirectory.length() == 0)) {
            initialDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        chooserIntent = new Intent(this, DirectoryChooserActivity.class);
        bindingIntent = new Intent(this, BindingKeysActivity.class);
        builder = DirectoryChooserConfig.builder();
        builder.newDirectoryName("DirChooserSample");
        builder.allowReadOnlyDirectory(true);
        builder.initialDirectory(initialDirectory);
        builder.allowNewDirectoryNameModification(true);
        final DirectoryChooserConfig config = builder
                .build();

        PATH_DIRECTORY = readInternal("data.txt", this);
        chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

        pathText = (TextView)findViewById(R.id.Path_TextView);
        keyText = (TextView)findViewById(R.id.key_pressed_TextViev);
        keyBindsButton = (Button)findViewById(R.id.button_keybinds_setting);

        directoryButton = (Button)findViewById(R.id.Change_directory_button_setting);


        // REQUEST_DIRECTORY is a constant integer to identify the request, e.g. 0
        directoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivityForResult(chooserIntent, 0);
            }
        });

        keyBindsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "keyBindsButton clicked");
                startActivity(bindingIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        pathText.setText("Path: " + PATH_DIRECTORY);
        CheckReadPermission(this, this);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                String dataString = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                try {
                    PATH_DIRECTORY = dataString.split("\n")[0];
                    if (PATH_DIRECTORY != null && new File(PATH_DIRECTORY).listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return pathname.isDirectory() && (pathname.listFiles(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String name) {
                                    return name.contains(".mp3")||name.contains(".wav");
                                }
                            }).length>0);
                        }
                    }).length < 1){
                        PATH_DIRECTORY = null;
                        Toast.makeText(this, "Папка не содержит папок с музыкой (.mp3 or .wav). Укажите другой путь", Toast.LENGTH_SHORT).show();
                    }
                }catch (NullPointerException e){
                    PATH_DIRECTORY = null;
                }
                pathText.setText("Path: " + PATH_DIRECTORY);
                if(PATH_DIRECTORY != null)
                    saveInternal("data.txt", PATH_DIRECTORY, this);
            } else {
                // Nothing selected
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
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
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        keyText.setText("KeyCode: "+ keyCode);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
