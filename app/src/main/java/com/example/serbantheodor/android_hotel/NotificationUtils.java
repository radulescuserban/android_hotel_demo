package com.example.serbantheodor.android_hotel;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Serban Theodor on 07-Mar-16.
 */
public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();
    private static String link;
    private static ArrayList<String> params;
    private static MyPreferenceManager preferenceManager;

    private Context mContext;

    public NotificationUtils() {
    }

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }


    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent, String flag) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        link = mContext.getString(R.string.send_message_to_server);

        params = new ArrayList<>();
        params.add(link);
        params.add(message);

        new task().execute(params);

        // notification icon
        final int icon = R.drawable.test3;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

        //final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
           //     + "://" + mContext.getPackageName() + "/raw/notification");

        final Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if(flag.equals("show")) {
            Log.d(TAG, "message received while app in background");
            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound, flag);
            //playNotificationSound();
        }
        else
            if(flag.equals("dont show"))
            {
                showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound, flag);
                Log.d(TAG, "message received while app in foreground");
                //Toast.makeText(mContext, "Message Received!", Toast.LENGTH_SHORT).show();
            }
    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound, String flag_background) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();


        if(QuickstartPreferences.appendNotificationMessages){
            // store the notification in shared pref first
            MyApplication.getInstance().getPrefManager().addNotification(message);

            // get the notifications from shared preferences
            String oldNotification = MyApplication.getInstance().getPrefManager().getNotifications();

            List<String> messages = Arrays.asList(oldNotification.split("\\|"));

            for (int i = messages.size() - 1; i >= 0; i--) {
                inboxStyle.addLine(messages.get(i));
            }
        }else
            inboxStyle.addLine(message);

        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                //.setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.ic_message_white_18dp)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setContentText(message)
                .build();

        notification.flags = Notification.FLAG_AUTO_CANCEL;

        if(flag_background.equals("show")) {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(QuickstartPreferences.NOTIFICATION_ID, notification);
        }
    }


    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.test3: R.mipmap.ic_launcher;
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + MyApplication.getInstance().getApplicationContext().getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(MyApplication.getInstance().getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */

    public static boolean isAppInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager) MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    class task extends AsyncTask<ArrayList<String>, String, String>
    {
        InputStream in = null;
        OutputStream os = null;
        String result = "";
        HashMap<String, String> postDataParams = new HashMap<>();

        protected void onPreExecute(){

        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {

            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");

                Log.d("DEBUG message:   " + entry.getKey() + " ", " " + entry.getValue());
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }


        @Override
        protected String doInBackground(ArrayList<String>... params){

            ArrayList<String> aux_params = new ArrayList<String>();
            aux_params = params[0];

            String link_task = aux_params.get(0);
            String message = aux_params.get(1);

            Log.d("DEBUG message received", link_task);
            //String link = "http://192.168.1.105:8080/android_hotel/login_check.php";
            URL url = null;
            try {
                url = new URL(link_task);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("DEBUG message received","url error");
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                Log.d("DEBUG message received", "postDataParams");

                //postDataParams.put("username", "pani");
                //postDataParams.put("password", "123");


                String reg_device_id, reception_id;
                MyPreferenceManager preferenceManager = MyApplication.getInstance().getPrefManager();
                reg_device_id = preferenceManager.get_reg_device_id();
                //Log.d("NOT UTIL reg_device_id ", reg_device_id);
                reception_id = preferenceManager.get_reception_id();

                postDataParams.put("from_user_id", reception_id);
                postDataParams.put("to_user_id", reg_device_id);
                postDataParams.put("message", message);
                Log.d("DEBUG BACKGROUND", reg_device_id);

                os = conn.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();


                in = new BufferedInputStream(conn.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));


                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line=reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }

                in.close();
                result = sb.toString();

                conn.disconnect();


            } catch (IOException e) {
                e.printStackTrace();
                Log.e("conexiune chat"," crapata");
            }

            return null;

        }

        protected void onPostExecute(String param){
            try {

                JSONObject Jobject = new JSONObject(result);
                String message_response = Jobject.getString("response");

                //Log.d("DEBUG CHAT POST", message_response);

                Message message_o = null;
                if(message_response == "true")
                {
                    String id = Jobject.getString("id");
                    String message = Jobject.getString("message");
                    String from_user_id = Jobject.getString("from_user_id");
                    String created_at = Jobject.getString("created_at");


                    message_o = new Message(id, message, from_user_id, created_at);
                }

                Intent resultIntent = new Intent("message_received_from_server");
                Log.d("sender", "Broadcasting message "+ message_o.getMessage() );
                resultIntent.putExtra("id", message_o.getId());
                resultIntent.putExtra("message", message_o.getMessage());
                resultIntent.putExtra("user_id", message_o.getUser_id());
                resultIntent.putExtra("created_at", message_o.getCreatedAt());

                String oldNotification = MyApplication.getInstance().getPrefManager().getNotifications();

                List<String> messages = Arrays.asList(oldNotification.split("\\|"));

                int total_messages_unread = messages.size();
                Log.d(TAG, total_messages_unread + "");
                preferenceManager = MyApplication.getInstance().getPrefManager();
                preferenceManager.add_unread_messages(total_messages_unread);
                Log.d(TAG, preferenceManager.get_unread_messages()+"");

                LocalBroadcastManager.getInstance(mContext).sendBroadcast(resultIntent);
            }
            catch (Exception e)
            {
                System.out.println("DEBUG chat - Exceptie in post execute!");
                e.printStackTrace();
            }

        }
    }



}
