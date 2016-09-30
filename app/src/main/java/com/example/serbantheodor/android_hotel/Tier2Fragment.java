package com.example.serbantheodor.android_hotel;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.StringTokenizer;


public class Tier2Fragment extends Fragment {

    private static String TAG = Tier2Fragment.class.getSimpleName();
    ArrayList<String> menu_label, menu_id, menu_icons, menu_page_id, menu_dynamic;
    GridView gv;

    String link, action;
    String location_id,parent_id,label, dynamic, page_id;

    public Tier2Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tier2
                , container, false);

        label = getArguments().getString("label");
        parent_id = getArguments().getString("parent_id");
        location_id = getArguments().getString("location_id");
        dynamic = getArguments().getString("dynamic");
        page_id = getArguments().getString("page_id");


        link = getString(R.string.populate_menu);

        new task2().execute(link);

        gv = (GridView) view.findViewById(R.id.grid_fragments_tier2);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {

                //Toast.makeText(view.getContext(), "" + position, Toast.LENGTH_SHORT).show();
                Tier3Fragment fragment = new Tier3Fragment();

                Bundle bundle = new Bundle();
                bundle.putString("page_id", menu_page_id.get(position));
                bundle.putString("parent_id", menu_id.get(position));
                bundle.putString("label", menu_label.get(position));
                bundle.putString("dynamic", menu_dynamic.get(position));

                fragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.tier_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }

        });

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.show();
        TextView hello = (TextView) actionBar.getCustomView().findViewById(R.id.title);
        hello.setText(label);

        // Inflate the layout for this fragment
        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        gv.setNumColumns(getResources().getInteger(R.integer.grid_rows));
    }

    void setTable(ArrayList<String> pmenu_id, ArrayList<String> pmenu_label, ArrayList<String> pmenu_icons,
                  ArrayList<String> pmenu_dynamic, ArrayList<String> menu_page_id, String location_id)
    {
        gv.setAdapter(new Tier2ItemAdapter(getView().getContext(), pmenu_id,pmenu_label,pmenu_icons,
                pmenu_dynamic, menu_page_id,location_id));
    }


    class task2 extends AsyncTask<String, String, Void>
    {
        private ProgressDialog dialog = new ProgressDialog(getActivity());
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

                Log.d(TAG + ":   " + entry.getKey() + " ", " " + entry.getValue());
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }


        @Override
        protected Void doInBackground(String... params){

            String link_task = params[0];


            Log.d(TAG, link_task);
            //String link = "http://192.168.1.105:8080/android_hotel/login_check.php";
            URL url = null;
            try {
                url = new URL(link_task);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("url error","url prost");
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Log.d(TAG, "page_id: " + parent_id + " location_id: " + location_id);
                postDataParams.put("parent_id", parent_id);
                postDataParams.put("location_id", location_id);

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
                menu_dynamic = new ArrayList<>();
                menu_page_id = new ArrayList<>();

                for(int i = 0 ; i < Jarray.length() ; i++)
                {
                    Jobject = Jarray.getJSONObject(i);
                    menu_page_id.add(Jobject.getString("page_id"));
                    menu_id.add(Jobject.getString("menu_id"));
                    menu_label.add(Jobject.getString("menu_label"));
                    menu_icons.add(Jobject.getString("menu_icon"));
                    menu_dynamic.add(Jobject.getString("menu_dynamic"));
                    Log.d("debug in post",menu_label.get(i));
                }

                setTable(menu_id, menu_label, menu_icons, menu_dynamic, menu_page_id, location_id);

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
