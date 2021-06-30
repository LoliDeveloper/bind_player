package com.example.media_player.services;

import android.app.Notification;
import android.content.Context;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.media_player.R;

public class CreateNotification {
    public static final String CHANNEL_ID = "chanell";

    public static final String ACTIONPREVIOUS= "actionprevious";
    public static final String CHANNEL_PLAY = "actionplay";
    public static final String CHANNEL_NEXT = "actionnext";

    public static Notification notification;

    public static void createNotification(Context context, Track track, int playbutton, int size){
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            //Bitmap icon = BitmapFactory.decodeResource(context.getResources(), track.getImage());

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                    .setContentTitle(track.getTitle())
                    .setContentText(track.getTrackDirectory())
                    .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();
            notificationManagerCompat.notify(1, notification);
        //}
    }
}
