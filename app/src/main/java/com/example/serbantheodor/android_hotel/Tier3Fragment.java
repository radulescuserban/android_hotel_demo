package com.example.serbantheodor.android_hotel;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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


public class Tier3Fragment extends Fragment {

    private static String TAG = Tier3Fragment.class.getSimpleName();

    private GridView gridView;
    public LinearLayout linearLayout;
    public String page_id, label, dynamic;
    private ArrayList<String> contentImageArrayList;
    private ArrayList<Integer> seqNoContentArrayList;
    private ArrayList<String> contentTypeArrayList;
    private ArrayList<String> contentTextArrayList;

    private ArrayList<ImageItem> images_ArrayList;
    private ArrayList<ImageItem> images_to_showArrayList;

    private ArrayList<Integer> images_seq_noArrayList;
    private ArrayList<String> images_filenameArrayList;
    private ArrayList<String> images_urlArrayList;
    private ArrayList<String> images_captionArrayList;
    int display_width, display_height;
    private String link;
    public MyPreferenceManager preferenceManager;
    private String directory_path;

    public Tier3Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tier3, container, false);
        linearLayout = (LinearLayout) v.findViewById(R.id.tier3_fragment);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        display_height = displaymetrics.heightPixels;
        display_width = displaymetrics.widthPixels;

        contentTypeArrayList = new ArrayList<>();
        seqNoContentArrayList = new ArrayList<>();
        contentTextArrayList = new ArrayList<>();
        contentImageArrayList = new ArrayList<>();


        label = getArguments().getString("label");
        page_id = getArguments().getString("page_id");
        dynamic = getArguments().getString("dynamic");

        preferenceManager = MyApplication.getInstance().getPrefManager();
        directory_path = preferenceManager.get_directory_for_images_path();
        images_ArrayList = new ArrayList<>();
        images_to_showArrayList = new ArrayList<>();

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.show();
        TextView hello = (TextView) actionBar.getCustomView().findViewById(R.id.title);
        hello.setText(label);
        Log.d("3frag on createview", page_id);

        images_filenameArrayList = new ArrayList<>();
        images_seq_noArrayList = new ArrayList<>();
        images_captionArrayList = new ArrayList<>();
        images_urlArrayList = new ArrayList<>();

        link = getString(R.string.get_content);
        Log.d(TAG, link);
        new task().execute(link);

        return v;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            display_height = displaymetrics.heightPixels;
            display_width = displaymetrics.widthPixels;
            int column_width = (display_width - 100) / 5;
            if(gridView != null)
                gridView.setColumnWidth(column_width);
            Log.d(TAG, "Screen:" + display_height + " - " + display_width);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            display_height = displaymetrics.heightPixels;
            display_width = displaymetrics.widthPixels;
            int column_width = (display_width - 100) / 5;
            if(gridView != null)
                gridView.setColumnWidth(column_width);
            Log.d(TAG, "Screen:" + display_height + " - " + display_width);
        }
    }


    public void setContentInPage()
    {
        for(int i = 0 ; i < contentTypeArrayList.size() ; i++)
        {
            if(contentTypeArrayList.get(i).equals("image"))
            {
                Log.d(TAG, i + "image");
                ImageView imageView = new ImageView(getActivity());
                LinearLayout.LayoutParams layoutParams = (new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                ));
                imageView.setAdjustViewBounds(true);

                linearLayout.addView(imageView, layoutParams);
                new DownloadImageTask(null, imageView, false).execute(contentImageArrayList.get(i));
            }
            else
            if(contentTypeArrayList.get(i).equals("title"))
            {
                Log.d(TAG, i + "title");
                TextView textView = new TextView(getActivity());

                LinearLayout.LayoutParams layoutParams = (new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                layoutParams.setMargins(20, 40, 20, 20);

                textView.setGravity(Gravity.CENTER);
                textView.setEms(24);
                int size = getResources().getInteger(R.integer.title_size_tier3);
                Log.d(TAG, size+"");
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
                textView.setTypeface(null, Typeface.BOLD);
                linearLayout.addView(textView, layoutParams);
                textView.setText(contentTextArrayList.get(i));
            }
            else
            if(contentTypeArrayList.get(i).equals("text"))
            {
                Log.d(TAG, (i) + "text");
                TextView textView = new TextView(getActivity());
                LinearLayout.LayoutParams layoutParams = (new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                layoutParams.setMargins(10, 20, 10, 20);
                textView.setPadding(10, 10, 10, 10);
                int size = getResources().getInteger(R.integer.text_size_tier3);
                Log.d(TAG, size+"");
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

                linearLayout.addView(textView, layoutParams);
                textView.setText(contentTextArrayList.get(i));
            } else {
                Log.d(TAG, i + "galley");
                GridView gv = new GridView(getActivity());
                LinearLayout.LayoutParams layoutParams = (new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                layoutParams.setMargins(10, 20, 10, 20);

                int no_images_to_show = images_to_showArrayList.size();
                int column_width = (display_width - 100) / no_images_to_show;
                gv.setColumnWidth(column_width);
                gv.setGravity(Gravity.CENTER);
                gv.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
                gv.setNumColumns(GridView.AUTO_FIT);
                gv.setVerticalSpacing(5);
                gv.setHorizontalSpacing(5);
                linearLayout.addView(gv, layoutParams);

                Log.d(TAG, images_to_showArrayList.size() + " - " + images_ArrayList.size());
                gv.setAdapter(new Tier3ImageAdapter(getView().getContext(), images_to_showArrayList, images_ArrayList.size()));

                gridView = gv;
                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {

                        Tier4FragmentImage fragment = new Tier4FragmentImage();

                        Bundle bundle = new Bundle();

                        bundle.putStringArrayList("array_of_filename", images_filenameArrayList);
                        bundle.putIntegerArrayList("array_of_seq_no", images_seq_noArrayList);
                        bundle.putStringArrayList("array_of_caption", images_captionArrayList);
                        bundle.putString("nr_of_images", images_ArrayList.size() + "");
                        Log.d(TAG, "position: " + position);
                        bundle.putInt("position_of_image", position);

                        fragment.setArguments(bundle);

                        FragmentManager fragmentManager = getFragmentManager();

                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.tier_fragment, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    }

                });
            }
        }
    }

    class task extends AsyncTask<String, String, Void>
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
                    task.this.cancel(true);
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

                Log.d("DEBUG:   " + entry.getKey() + " ", " " + entry.getValue());
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }


        @Override
        protected Void doInBackground(String... params){

            String link_task = params[0];


            Log.d("debug", link_task);
            //String link = "http://192.168.1.105:8080/android_hotel/login_check.php";
            URL url = null;
            try {
                url = new URL(link_task);
                //Log.d(TAG, "bag url-ul");
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

                Log.d(TAG, dynamic + " - " + preferenceManager.get_dynamic_page_id());

                if(dynamic.equals("0"))
                    postDataParams.put("page_id", page_id);
                else
                {
                    String dynamic_page_id = preferenceManager.get_dynamic_page_id()+"";
                    postDataParams.put("page_id", dynamic_page_id);
                }

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

                Log.d("debug result ",result);

                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("conexiune", " crapata");
            }
            return null;
        }

        protected void onPostExecute(Void v){
            try {
                String caption, url, seq_no, content_type, content_text, no_images_in_galleryS;
                int no_images_in_gallery;
                ImageItem imageItem;

                JSONArray Jarray = new JSONArray(result);
                JSONObject Jobject;
                for(int i = 0 ; i < Jarray.length() ; i ++)
                {
                    contentImageArrayList.add(i, "");
                    contentTypeArrayList.add(i, "");
                    contentTextArrayList.add(i, "");
                    seqNoContentArrayList.add(i, 0);
                }
                //Log.d(TAG, contentTypeArrayList.size() + "contents");
                Log.d(TAG, Jarray.length() + "");
                for(int i = 0 ; i < Jarray.length() ; i++)
                {
                    Jobject = Jarray.getJSONObject(i);
                    content_type = Jobject.getString("type");

                    //Log.d(TAG, content_type);
                    if(content_type.equals("title") || content_type.equals("text"))
                    {
                        content_text = Jobject.getString("content");
                        contentTypeArrayList.set(i, content_type);
                        contentTextArrayList.set(i, content_text);
                        seqNoContentArrayList.set(i, i);
                    }
                    else
                    if(content_type.equals("image"))
                    {
                        seqNoContentArrayList.set(i, i);
                        url = Jobject.getString("content");
                        contentTypeArrayList.set(i, content_type);
                        contentImageArrayList.set(i, url);
                    }
                    else
                    if(content_type.equals("gallery"))
                    {
                        no_images_in_galleryS = Jobject.getString("elements");
                        no_images_in_gallery = Integer.parseInt(no_images_in_galleryS) - 1;

                        for(int j = 0 ; j <= no_images_in_gallery ; j ++) {
                            JSONObject jsonObjectGallery = Jobject.getJSONObject(j + "");
                            seq_no = jsonObjectGallery.getString("seq_no");
                            url = jsonObjectGallery.getString("url");
                            caption = jsonObjectGallery.getString("caption");

                            int seq_no_int = Integer.parseInt(seq_no);
                            imageItem = new ImageItem(url, "", caption, seq_no_int);
                            images_urlArrayList.add(url);
                            images_captionArrayList.add(caption);

                            if(images_to_showArrayList.size() <= 4)
                            {
                                images_to_showArrayList.add(imageItem);
                            }
                            images_ArrayList.add(imageItem);

                        }
                        seqNoContentArrayList.set(i, i);
                        contentTypeArrayList.set(i, content_type);

                        for(int k = 0 ; k < images_ArrayList.size() ; k++)
                        {
                            String filename;
                            String[] imageItemParsed = images_ArrayList.get(k).getUrl().split("/");
                            filename = imageItemParsed[imageItemParsed.length - 1];
                            Log.d(TAG, "" +  filename);
                            images_ArrayList.get(k).setFilename(filename);

                            int seq_no_int;
                            seq_no_int = images_ArrayList.get(k).getSeq_no();

                            images_seq_noArrayList.add(seq_no_int);
                            images_filenameArrayList.add(filename);
                        }

                    }
                }
                //Log.d(TAG, contentTypeArrayList.size() + "contents");

                Log.d(TAG, images_ArrayList.size() + "");
                for(int i = 0 ; i < images_ArrayList.size() ; i++)
                {
                    Log.d(TAG, images_ArrayList.get(i).getFilename() + images_ArrayList.get(i).getUrl());

                    String filename = images_ArrayList.get(i).getFilename();
                    File image = new File(directory_path, filename);
                    Log.d(TAG, "debug galerie 99999 file:" + filename + "exista? " + image.exists());
                    if(!image.exists())
                    {
                        new DownloadImageTask(images_ArrayList.get(i), null, true)
                                .execute(images_ArrayList.get(i).getUrl());
                    }
                }
                setContentInPage();

            }
            catch (Exception e)
            {
                System.out.println("Exceptie in post execute!");
                e.printStackTrace();
            }
            this.dialog.dismiss();

        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage, String filename){

        String directory_path = preferenceManager.get_directory_for_images_path();
        File mypath = new File(directory_path, filename);
        Log.d(TAG, mypath.getAbsolutePath());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 70, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "image saved");
        return mypath.getAbsolutePath();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        boolean save_image;
        ImageItem imageItem;

        public DownloadImageTask(ImageItem imageItem, ImageView bmImage, boolean save_image) {
            this.save_image = save_image;
            this.imageItem = imageItem;
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Log.d("Tier 3 ", urldisplay);
            Bitmap bitmap_image = null;
            InputStream in = null;

            try {
                in = new java.net.URL(urldisplay).openStream();
                bitmap_image = BitmapFactory.decodeStream(in);
                Log.d("Tier3 ", bitmap_image.toString());
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            return bitmap_image;
        }

        protected void onPostExecute(Bitmap result) {
            //Log.d("Tier 3 ", result.toString());
            if(save_image)
                saveToInternalStorage(result, imageItem.getFilename());
            else
                bmImage.setImageBitmap(result);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
