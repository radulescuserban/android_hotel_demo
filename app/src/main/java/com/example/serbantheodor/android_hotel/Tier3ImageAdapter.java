package com.example.serbantheodor.android_hotel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Serban Theodor on 17-Apr-16.
 */
public class Tier3ImageAdapter extends BaseAdapter {

    private static String TAG = Tier3ImageAdapter.class.getSimpleName();
    private Context context;
    private int gallery_size;
    private final ArrayList<ImageItem> images_ArrayList;
    MyPreferenceManager preferenceManager;
    private String directory_path;


    public Tier3ImageAdapter(Context context, ArrayList<ImageItem> images_url, int gallery_size) {
        this.context = context;
        this.gallery_size = gallery_size;
        this.images_ArrayList = images_url;
    }

    @Override
    public int getCount() {
        return images_ArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        preferenceManager = MyApplication.getInstance().getPrefManager();


        if (convertView == null) {
            gridView = new View(context);
            String filename;

            Log.d(TAG, position + " - " + getCount());

            if((position == getCount() - 1) && (getCount() >= 4)) {
                gridView = inflater.inflate(R.layout.tier3_last_image_item, null);
            }
            else {
                gridView = inflater.inflate(R.layout.tier3_image_item, null);

                ImageView imageView = (ImageView) gridView
                        .findViewById(R.id.tier3_image_button_view);

                Bitmap image_val;
                filename = images_ArrayList.get(position).getFilename();
                directory_path = preferenceManager.get_directory_for_images_path();
                Log.d(TAG, images_ArrayList.get(position).getFilename());
                File image = new File(directory_path, filename);
                Log.d(TAG, "debug galerie 99999 file:" + filename + "exista? " + image.exists());

                if(image.exists())
                {
                    image_val = loadImageFromStorage(directory_path, filename);
                    imageView.setImageBitmap(image_val);
                }
                else
                {
                    new DownloadImageTask(imageView, images_ArrayList.get(position))
                            .execute(images_ArrayList.get(position).getUrl());
                }
            }


        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String filename){
        String directory_path = preferenceManager.get_directory_for_images_path();
        //Log.d(TAG, directory_path);
        File mypath = new File(directory_path, filename);
        Log.d(TAG, mypath.getAbsolutePath());


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
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

    private Bitmap loadImageFromStorage(String directory_path, String filename)
    {
        Bitmap b = null;
        try {
            Log.d(TAG, directory_path + " - " + filename);
            File f = new File(directory_path, filename);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        Log.d(TAG, "image loaded");
        return b;

    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ImageItem imageItem;

        public DownloadImageTask(ImageView bmImage, ImageItem imageItem) {
            this.bmImage = bmImage;
            this.imageItem = imageItem;
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

            saveToInternalStorage(result, imageItem.getFilename());
            bmImage.setImageBitmap(result);
        }
    }

}
