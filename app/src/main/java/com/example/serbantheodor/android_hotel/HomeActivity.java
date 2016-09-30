package com.example.serbantheodor.android_hotel;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Serban Theodor on 03-Dec-15.
 */
public class HomeActivity extends AppCompatActivity {

    private static String TAG = HomeActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 10000;
    static String PARENT_ZERO = "0";
    String directory_for_images;
    String location_id;
    String link;
    String action;
    String android_id, room_name, gcm_token;
    ArrayList<String> menu_label, menu_id, menu_icons, menu_dynamic_page, page_id;
    TextView unread_messages_textView;
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout);

        if(!isNetworkAvailable())
        {
            Intent intent = new Intent(HomeActivity.this,
                    CheckErrorActivity.class).putExtra("error_type",
                    "no internet");
            startActivity(intent);
        }

        gridView = (GridView) findViewById(R.id.grid_fragments_home);

        MyPreferenceManager preferenceManager = MyApplication.getInstance().getPrefManager();
        directory_for_images = create_director_for_images();
        if(!directory_for_images.equals(""))
            preferenceManager.add_directory_for_images_path(directory_for_images);
        Log.d(TAG, preferenceManager.get_directory_for_images_path() + "");

        gcm_token = preferenceManager.get_gcm_token();

        if (checkPlayServices()) {
            Intent intent = new Intent(this,
                    MyInstanceIDListenerService.class);
            startService(intent);
        }

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, android_id);

        preferenceManager.add_android_id(android_id);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                startActivity(intent);
            }

        });

        updateUnreadCount();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_view);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM
                | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);

        actionBar.setDisplayHomeAsUpEnabled(true);

        action = "device_check";
        link = getString(R.string.device_check);

        ArrayList<String> params = new ArrayList<>();
        params.add(action);
        params.add(link);
        new task().execute(params);
    }

    private BroadcastReceiver MessageReceivedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUnreadCount();
            Log.d("Home ", "broadcast received");
        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                MessageReceivedBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                MessageReceivedBroadcastReceiver, new IntentFilter("message_received_from_server"));

        updateUnreadCount();
        // clearing the notification tray
        NotificationUtils.clearNotifications();
    }


    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(MessageReceivedBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        gridView.setNumColumns(getResources().getInteger(R.integer.grid_rows));
    }

    void updateUnreadCount()
    {
        MyPreferenceManager preferenceManager = MyApplication.getInstance().getPrefManager();
        int unread_messages = preferenceManager.get_unread_messages();
        unread_messages_textView = (TextView) findViewById(R.id.unread_messages_textView);

        Log.d("Home unread messages", unread_messages + "");
        if(unread_messages == 0)
        {
            unread_messages_textView.setVisibility(View.INVISIBLE);
        }
        else
        {
            String s = unread_messages + "";
            unread_messages_textView.setText(s);
            unread_messages_textView.setVisibility(View.VISIBLE);
        }

    }


    void setTable(ArrayList<String> pmenu_id,ArrayList<String> pmenu_label,ArrayList<String> pmenu_icons,
                  ArrayList<String> pmenu_dynamic , ArrayList<String> ppage_id, String location_id)
    {
        gridView.setAdapter(new HomeItemAdapter(this, pmenu_id, pmenu_label, pmenu_icons, pmenu_dynamic,
                ppage_id, location_id));
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Home Activity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public String create_director_for_images()
    {
        File directory = new File(Environment.getExternalStorageDirectory()
                + "/gallery/");

        String my_director_path = "";
        if(!directory.exists()) {
            directory.mkdir();
            my_director_path = directory.getAbsolutePath();
            return my_director_path;
        }
        else
        {
            Log.d(TAG, "file exists");
            return directory.getAbsolutePath();
        }
    }

    class task extends AsyncTask<ArrayList<String>, String, String>
    {
        private ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        InputStream in = null;
        OutputStream os = null;
        String result = "";
        HashMap<String, String> postDataParams = new HashMap<>();


        protected void onPreExecute(){
            dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface arg0){
                    task.this.cancel(true);
                }
            });
        }

        private String getPostDataString(HashMap<String, String> params)
                throws UnsupportedEncodingException {

            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");

                Log.d("DEBUG:   "+entry.getKey()+" "," "+entry.getValue());
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

            String action_task = aux_params.get(0);
            String link_task = aux_params.get(1);

            URL url = null;
            try {
                url = new URL(link_task);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                if(action_task.equals("device_check")) {
                    postDataParams.put("android_id", android_id);
                }

                os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
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
                Log.e("conexiune"," crapata");
            }

            return action_task;
        }

        protected void onPostExecute(String param){
            try {

                String action_task = param;
                if(action_task.equals("device_check")) {
                    JSONObject Jobject = new JSONObject(result);
                    String reg_device_id;
                    String reception_id;
                    String dynamic_page_id;
                    String resort_id;

                    MyPreferenceManager preferenceManager = MyApplication.getInstance().getPrefManager();

                    String logged_in = Jobject.getString("logged_in");
                    String reg_status = Jobject.getString("reg_status");



                    if (logged_in != "true") {

                        if(reg_status == "false")
                        {
                            preferenceManager.add_registration_sent(false);
                        }
                        else
                        {
                            preferenceManager.add_registration_sent(true);
                        }

                        Intent intent = new Intent(HomeActivity.this, NotRegisteredActivity.class).putExtra("android_id", android_id);
                        intent.putExtra("gcm_token", gcm_token);
                        Log.d(TAG, "logged_in false");
                        startActivity(intent);
                    } else {

                        preferenceManager.add_registration_sent(false);
                        reg_device_id = Jobject.getString("reg_device_id");
                        reception_id = Jobject.getString("reception_user_id");
                        dynamic_page_id = Jobject.getString("dynamic_page_id");
                        resort_id = Jobject.getString("location_id");
                        Log.d(TAG, dynamic_page_id);

                        preferenceManager.add_reception_id(reception_id);

                        room_name = Jobject.getString("name");
                        location_id = "1";

                        int dynamic_page_id_to_int = Integer.parseInt(dynamic_page_id);
                        preferenceManager.add_dynamic_page_id(dynamic_page_id_to_int);
                        preferenceManager.add_reg_device_id(reg_device_id);


                        Log.d(TAG, "logged_in true");
                        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                        TextView hello = (TextView) actionBar.getCustomView().findViewById(R.id.title);
                        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM
                                | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);

                        hello.setText(room_name);
                        link = getString(R.string.populate_menu);
                        new task2().execute(link);
                    }
                }

            }
            catch (Exception e)
            {
                System.out.println("Exceptie in post execute!");
                e.printStackTrace();
            }
            this.dialog.dismiss();
        }
    }





    class task2 extends AsyncTask<String, String, Void>
    {
        private ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        InputStream in = null;
        OutputStream os = null;
        String result = "";
        HashMap<String, String> postDataParams = new HashMap<>();


        protected void onPreExecute(){

            dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface arg0){
                    task2.this.cancel(true);
                }
            });
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {

            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");

                //Log.d("DEBUG:   "+entry.getKey()+" "," "+entry.getValue());
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }



        @Override
        protected Void doInBackground(String... params){

            String link_task = params[0];


            //Log.d("debug", link_task);
            //String link = "http://192.168.1.105:8080/android_hotel/login_check.php";
            URL url = null;
            try {
                url = new URL(link_task);
                //Log.d("aici ", "bag url-ul");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                //Log.d("url error","url prost");
            }
            HttpURLConnection conn = null;
            try {
                //Log.d("debug","inceputul try-ului");
                conn = (HttpURLConnection) url.openConnection();
                //Log.d("debug", "dupa connect");
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //Log.d("debug", "in action populate_menu");
                postDataParams.put("parent_id",PARENT_ZERO);
                postDataParams.put("location_id",location_id);

                os = conn.getOutputStream();

                //Log.d("debug", "BufferedWriter");
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
                Log.d(TAG, result);
                conn.disconnect();


            } catch (IOException e) {
                e.printStackTrace();
                Log.e("conexiune"," crapata");
            }

            return null;
        }

        protected void onPostExecute(Void v){
            try {
                JSONArray Jarray = new JSONArray(result);
                JSONObject Jobject = new JSONObject();

                menu_id = new ArrayList<String>();
                menu_label = new ArrayList<String>();
                menu_icons = new ArrayList<String>();
                menu_dynamic_page = new ArrayList<String>();
                page_id = new ArrayList<String>();

                for(int i = 0 ; i < Jarray.length() ; i++)
                {
                    Jobject = Jarray.getJSONObject(i);
                    page_id.add(Jobject.getString("page_id"));
                    menu_id.add(Jobject.getString("menu_id"));
                    menu_label.add(Jobject.getString("menu_label"));
                    menu_icons.add(Jobject.getString("menu_icon"));
                    menu_dynamic_page.add(Jobject.getString("menu_dynamic"));
                }
                setTable(menu_id, menu_label, menu_icons, menu_dynamic_page, page_id, location_id);
            }
            catch (Exception e)
            {
                System.out.println("Exceptie in post execute!");
                e.printStackTrace();
            }
            this.dialog.dismiss();
        }
    }

}
