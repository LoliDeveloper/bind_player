package com.example.media_player;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.media_player.binds.KeyBind;

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
import java.util.Iterator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.ContextCompat.startActivity;

public class CommonFunctions {
    public static void CheckReadPermission(Activity activity, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            }
        }
    }
    public static void saveInternal(String file_name, String data, Activity activity) {
        try {
            FileOutputStream fileOutputStream = activity.openFileOutput(file_name, MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static String readInternal(String file_name, Activity activity) {
        try{
            FileInputStream fileInputStream = activity.openFileInput(file_name);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            String lines;

            while((lines = bufferedReader.readLine())!=null)
            {
                stringBuffer.append(lines + "\n");
            }
            return stringBuffer.toString();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkAccessibilityPermission(Context context) {
        int accessEnabled=0;
        try {
            accessEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (accessEnabled==0) {
            /** if not construct intent to request permission */
            Toast.makeText(context, "Необходимо включить специальные возможности", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            /** request permission via start activity for result */
            startActivity(context, intent, null);
            return false;
        } else {
            return true;
        }
    }

    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }
    public static ArrayList<KeyBind> CreateKeyBindsForSerializationList(Context context, String TAG, String bindingFileName) {
        ArrayList<KeyBind> keyBindsForSerializationList = new ArrayList<>();
        if(bindingFileName != null)
            try{
                FileInputStream fileInputStream
                        = context.openFileInput(bindingFileName);
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
        return keyBindsForSerializationList;
    }
    public static void resetBindingsInternal(Context context, String TAG, String bindingFileName,ArrayList<KeyBind> keyBindsForSerializationList) {
        try {

            FileOutputStream fileOutputStream
                    = context.openFileOutput(bindingFileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(keyBindsForSerializationList);
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

    public static File[] createPlaylistFiles(String directory_path){
        return
                new File(directory_path).listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isDirectory() && (pathname.listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                return name.contains(".mp3")||name.contains(".wav");
                            }
                        }).length>0);
                    }
                });

    }


    public static String generateCoolString(int[] keyCodes) {
        String s = "";
        if(keyCodes.length < 1){
            return "";
        }
        String[] tmp = KeyEvent.keyCodeToString(keyCodes[0]).replace('_',' ').split("KEYCODE ");
        String add = KeyEvent.keyCodeToString(keyCodes[0]);
        if(tmp.length > 1){
            add = tmp[1];
        }
        add = add.substring(0, 1).toUpperCase() + add.substring(1).toLowerCase();
        s += (add);
        for(int i = 1;i < keyCodes.length;++i) {
            tmp = KeyEvent.keyCodeToString(keyCodes[i]).replace('_',' ').split("KEYCODE ");
            add = KeyEvent.keyCodeToString(keyCodes[i]);
            if(tmp.length > 1){
                add = tmp[1];
            }
            add = add.substring(0, 1).toUpperCase() + add.substring(1).toLowerCase();
            s += " + " + (add);
        }
        return s;
    }
    public static boolean removeSimilarBinds(ArrayList<KeyBind> keyBindsForSerializationList, int[] keyCodes) {
        Iterator<KeyBind> i=keyBindsForSerializationList.iterator();
        while(i.hasNext()) {
            KeyBind keyBind = i.next();
            if(Arrays.equals(keyBind.getKeyCodes(), keyCodes)){
                i.remove();
                //keyBindsForSerializationList.remove(keyBind);
            }
        }
        return true;
    }
}
