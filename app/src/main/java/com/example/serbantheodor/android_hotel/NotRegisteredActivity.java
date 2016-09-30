package com.example.serbantheodor.android_hotel;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
 * Created by Serban Theodor on 26-Jul-16.
 */
public class NotRegisteredActivity extends AppCompatActivity {

    private static String TAG = NotRegisteredActivity.class.getSimpleName();
    EditText register_text_et;
    TextView title, error_text, registration_status;
    Button register_button, retry_button;
    String android_id, gcm_token, action, link, register_text;

    MyPreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_registered);

        preferenceManager = MyApplication.getInstance().getPrefManager();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_view);
        TextView hello = (TextView) actionBar.getCustomView().findViewById(R.id.title);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM
                | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);

        hello.setText("Register device");


        register_button = (Button) findViewById(R.id.register_btn);
        retry_button = (Button) findViewById(R.id.retry_btn);
        title = (TextView) findViewById(R.id.title_tv);
        error_text = (TextView) findViewById(R.id.error_tv);
        registration_status = (TextView) findViewById(R.id.registration_status);
        register_text_et = (EditText) findViewById(R.id.register_text_et);

        title.setText(R.string.not_registered_title);
        error_text.setText(R.string.not_registered_message);
        registration_status.setText(R.string.registration_request_sent);

        android_id = getIntent().getStringExtra("android_id");
        gcm_token = getIntent().getStringExtra("gcm_token");


        boolean register_status_bool = preferenceManager.get_registration_sent();
        if(register_status_bool)
        {
            register_button.setVisibility(View.INVISIBLE);
            registration_status.setVisibility(View.VISIBLE);
            retry_button.setVisibility(View.VISIBLE);
            register_text_et.setVisibility(View.INVISIBLE);
        }
        else
        {
            register_text_et.setVisibility(View.VISIBLE);
            retry_button.setVisibility(View.INVISIBLE);
            register_button.setVisibility(View.VISIBLE);
            registration_status.setVisibility(View.INVISIBLE);
        }


        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "register_device";
                link = getString(R.string.register_device);

                register_text = register_text_et.getText().toString();
                Log.d(TAG, register_text);
                preferenceManager.add_registration_sent(true);

                ArrayList<String> params = new ArrayList<>();
                params.add(action);
                params.add(link);
                new task().execute(params);
            }
        });

        retry_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NotRegisteredActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }


    class task extends AsyncTask<ArrayList<String>, String, String>
    {
        private ProgressDialog dialog = new ProgressDialog(NotRegisteredActivity.this);
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

                postDataParams.put("android_id", android_id);
                postDataParams.put("gcm_token", gcm_token);
                postDataParams.put("register_text", register_text);


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
                JSONObject Jobject = new JSONObject(result);
                String reg_sent = Jobject.getString("registration_sent");
                boolean register_status_bool = preferenceManager.get_registration_sent();
                if(register_status_bool)
                {
                    register_button.setVisibility(View.INVISIBLE);
                    registration_status.setVisibility(View.VISIBLE);
                    retry_button.setVisibility(View.VISIBLE);
                    register_text_et.setVisibility(View.INVISIBLE);
                }
                else
                {
                    register_text_et.setVisibility(View.VISIBLE);
                    retry_button.setVisibility(View.INVISIBLE);
                    register_button.setVisibility(View.VISIBLE);
                    registration_status.setVisibility(View.INVISIBLE);
                }

                Log.d(TAG, "post execute");
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
