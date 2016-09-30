package com.example.serbantheodor.android_hotel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Serban Theodor on 07-Mar-16.
 */
public class ChatActivity extends AppCompatActivity {

    private static String TAG = ChatActivity.class.getSimpleName();
    private ArrayList<String> params, params2;

    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MessageAdapter mAdapter;
    private ArrayList<Message> messageArrayList, aux_messageArrayList;
    private EditText inputMessage;
    private Button btn_send;
    private String message, link, token;
    private Message message_o;
    private String last_msg_id, previous_last_msg_id;
    private int message_position_chat = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_layout);
        Log.d(TAG, "onCreate");

        MyPreferenceManager preferenceManager = MyApplication.getInstance().getPrefManager();
        token = preferenceManager.get_gcm_token();

        preferenceManager.add_unread_messages(0);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_view);
        TextView hello = (TextView) actionBar.getCustomView().findViewById(R.id.title);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM
                | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);

        hello.setText(R.string.chat_title);
        actionBar.setDisplayHomeAsUpEnabled(true);


        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.chat_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populate_recycler();
            }
        });


        messageArrayList = new ArrayList<>();

        //get messages to show in recycler

        populate_recycler();

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);

        mAdapter = new MessageAdapter(this, messageArrayList);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        MyApplication.getInstance().clear_notification_messages();

        btn_send = (Button) findViewById(R.id.send);
        link = getString(R.string.send_message_to_server);

        params = new ArrayList<>();
        params.add(link);

        inputMessage = (EditText) findViewById(R.id.message);

        btn_send.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        message = inputMessage.getText().toString();
                        if (!message.isEmpty())
                            new task().execute(params);
                        inputMessage.setText("");
                    }
                }
        );
    }

    private BroadcastReceiver MessageReceivedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            MyPreferenceManager preferenceManager = MyApplication.getInstance().getPrefManager();

            preferenceManager.add_unread_messages(0);

            String message_id = intent.getStringExtra("id");
            String message_content = intent.getStringExtra("message");
            String message_user_id = intent.getStringExtra("user_id");
            String message_created_at = intent.getStringExtra("created_at");

            Message message = new Message(message_id, message_content, message_user_id, message_created_at);

            Log.d(TAG, "message received from notification");
            Log.d("Chat receiver", message_id + " " + message_content + " " + message_user_id + " " + message_created_at);

            if (!message.getMessage().equals("")) {


                messageArrayList.add(message);
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, " in receiver se introduce mesajul");
                if (mAdapter.getItemCount() >= 1) {

                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                }
            }

        }
    };

    @Override
    protected void onResume() {
        // Register to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(
                MessageReceivedBroadcastReceiver, new IntentFilter("message_received_from_server"));
        super.onResume();
        Log.d(TAG, "onResume");
        MyApplication.getInstance().clear_notification_messages();

        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(
        //        MessageReceivedBroadcastReceiver);

        MyPreferenceManager preferenceManager = MyApplication.getInstance().getPrefManager();

        preferenceManager.add_unread_messages(0);
        preferenceManager.clear_messages();
        
        Log.d(TAG, "onPause");
        Log.d(TAG, ""+preferenceManager.get_unread_messages());

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        Log.d(TAG, "onDestroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(MessageReceivedBroadcastReceiver);

        MyPreferenceManager preferenceManager = MyApplication.getInstance().getPrefManager();

        preferenceManager.add_unread_messages(0);
        preferenceManager.clear_messages();

        Log.d(TAG, ""+preferenceManager.get_unread_messages());

        super.onDestroy();
    }


    private void populate_recycler()
    {
        link = getString(R.string.get_all_messages);

        params2 = new ArrayList<>();
        params2.add(link);
        params2.add(last_msg_id);

        new task2().execute(params2);

        swipeRefreshLayout.setRefreshing(false);
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

                Log.d("DEBUG chat:   " + entry.getKey() + " ", " " + entry.getValue());
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

            Log.d("DEBUG chat", link_task);
            //String link = "http://192.168.1.105:8080/android_hotel/login_check.php";
            URL url = null;
            try {
                url = new URL(link_task);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("DEBUG chat","url error");
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                Log.d("DEBUG chat", "postDataParams");

                //postDataParams.put("username", "pani");
                //postDataParams.put("password", "123");

                String reg_device_id, reception_id;
                MyPreferenceManager preferenceManager = MyApplication.getInstance().getPrefManager();
                reg_device_id = preferenceManager.get_reg_device_id();
                reception_id = preferenceManager.get_reception_id();

                postDataParams.put("to_user_id", reception_id);
                postDataParams.put("from_user_id", reg_device_id);
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

                Log.d("DEBUG CHAT POST", message_response);

                if(message_response == "true")
                {
                    String id = Jobject.getString("id");
                    String message = Jobject.getString("message");
                    String from_user_id = Jobject.getString("from_user_id");
                    String created_at = Jobject.getString("created_at");


                    message_o = new Message(id, message, from_user_id, created_at);
                    messageArrayList.add(message_o);

                    mAdapter.notifyDataSetChanged();
                    if (mAdapter.getItemCount() > 1) {
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println("DEBUG chat - Exceptie in post execute!");
                e.printStackTrace();
            }

        }
    }

    //task pentru a returna toate mesajele


    class task2 extends AsyncTask<ArrayList<String>, String, String>
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

                Log.d("DEBUG chat:   " + entry.getKey() + " ", " " + entry.getValue());
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
            String last_message_id = aux_params.get(1);

            URL url = null;
            try {
                url = new URL(link_task);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("DEBUG chat","url error");
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String reg_device_id;
                MyPreferenceManager preferenceManager = MyApplication.getInstance().getPrefManager();
                reg_device_id = preferenceManager.get_reg_device_id();

                if(last_message_id == null)
                {
                    last_message_id = "";
                }

                postDataParams.put("last_message_id", last_message_id);
                postDataParams.put("reg_device_id", reg_device_id);
                Log.d(TAG,"last_message_id: " + last_message_id);
                Log.d(TAG,"reg_device_id: " + reg_device_id);

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

            aux_messageArrayList = new ArrayList<>();
            try {

                Log.d(TAG, result);
                JSONObject Jobject = new JSONObject(result);

                String message_response = Jobject.getString("response");

                Log.d(TAG, "Response " + message_response);

                if(last_msg_id != null)
                {
                    for(int j = 0 ; j < messageArrayList.size() ; j++)
                    {
                        aux_messageArrayList.add(messageArrayList.get(j));
                    }
                    messageArrayList.clear();
                }


                if(message_response.equals("true"))
                {
                    JSONArray jArray = new JSONArray(Jobject.getString("messages"));
                    for (int i = 0 ; i < jArray.length(); i++) {
                        JSONArray ja= jArray.getJSONArray(i);

                        String id = ja.getString(0);
                        String message = ja.getString(1);
                        String from_user_id = ja.getString(2);
                        String created_at = ja.getString(3);

                        message_o = new Message(id, message, from_user_id, created_at);

                        messageArrayList.add(message_o);

                        if(i == 0)
                        {
                            previous_last_msg_id = last_msg_id;
                            last_msg_id = id;
                        }
                    }
                    Log.d(TAG, "Messages received: " + messageArrayList.size());
                    Log.d(TAG, "All messages: " + aux_messageArrayList.size());


                    for(int i = 0 ; i < aux_messageArrayList.size() ; i ++)
                    {
                        messageArrayList.add(aux_messageArrayList.get(i));
                    }

                    Log.d("CHAT LAST ID ", last_msg_id+ "-" + previous_last_msg_id);

                    mAdapter.notifyDataSetChanged();



                    if (mAdapter.getItemCount() > 1) {
                        if(previous_last_msg_id != null) {
                            Log.d("CHAT 1 ", Integer.parseInt(previous_last_msg_id)+"");
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - message_position_chat);
                            message_position_chat += messageArrayList.size();
                        }
                        else
                        {
                            Log.d("CHAT 2 ", (mAdapter.getItemCount() - 1)+"");
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }
                    }

                }
            }
            catch (Exception e)
            {
                System.out.println("DEBUG chat - Exceptie in post execute!");
                e.printStackTrace();
            }

        }
    }

}
