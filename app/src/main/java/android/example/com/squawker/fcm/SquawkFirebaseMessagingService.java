package android.example.com.squawker.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class SquawkFirebaseMessagingService extends FirebaseMessagingService {

    private final int mNotificationId = 44;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> squawk = remoteMessage.getData();

        if (!squawk.isEmpty()) {
            String test = squawk.get("Test");
            String author = squawk.get(SquawkContract.COLUMN_AUTHOR);
            String authorKey = squawk.get(SquawkContract.COLUMN_AUTHOR_KEY);
            String message = squawk.get(SquawkContract.COLUMN_MESSAGE);
            String date = squawk.get(SquawkContract.COLUMN_DATE);

            notifyUser(author, message);
            insertSquawk(author, authorKey, message, date);
        }
    }

    private void notifyUser(String title, String message){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_duck)
                        .setContentTitle(title)
                        .setContentText(message);

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager != null) {
            mNotificationManager.notify(mNotificationId, mBuilder.build());
        }
    }

    private void insertSquawk(String author, String authorKey, String message, String date){
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();

        values.put(SquawkContract.COLUMN_AUTHOR, author);
        values.put(SquawkContract.COLUMN_AUTHOR_KEY, authorKey);
        values.put(SquawkContract.COLUMN_MESSAGE, message);
        values.put(SquawkContract.COLUMN_DATE, date);

        resolver.insert(SquawkProvider.SquawkMessages.CONTENT_URI, values);
    }
}