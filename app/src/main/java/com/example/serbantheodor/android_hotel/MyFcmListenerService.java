package com.example.serbantheodor.android_hotel;

/**
 * Created by Serban Theodor on 03-Mar-16.
 */


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyFcmListenerService";

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();
        Map data = message.getData();
        Log.d(TAG, "From: " + from);

        Log.i(TAG, data.toString());
        String title = data.get("title").toString();
        String message_data = data.get("data").toString();
        Log.i(TAG, data.get("data").toString());
        sendNotification(title, message_data);
    }

    private void sendNotification(String title, String data) {

            try {
                JSONObject datObj = new JSONObject(data);

                JSONObject mObj = datObj.getJSONObject("message");

                String message = mObj.getString("message");

                Intent resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                String flag;

                if (NotificationUtils.isAppInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    //resultIntent.setAction("message_from_server");
                    //Log.d("sender", "Broadcasting message "+ message );
                    //resultIntent.putExtra("message", message);
                    //resultIntent.putExtra("to_user_id", to_user_id);
                    //resultIntent.putExtra("from_user_id", from_user_id);
                    //LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);

                    flag = "show";
                    Log.d(TAG, flag);
                    showNotificationMessage(getApplicationContext(), title, message, "", resultIntent, flag);
                }
                else
                {
                    // app is in background. show the message in notification try
                    flag = "dont show";
                    Log.d(TAG, flag);

                    showNotificationMessage(getApplicationContext(), title, message, "", resultIntent, flag);
                }

            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    /*
    private void sendNotification(String message) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.test3)
                .setContentTitle("Android Hotel")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification , notificationBuilder.build());
    }
*/

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent, String flag) {

        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, flag);
    }

}

