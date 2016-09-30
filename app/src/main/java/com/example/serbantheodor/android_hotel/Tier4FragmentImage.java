package com.example.serbantheodor.android_hotel;

import android.app.Fragment;
import android.app.usage.UsageEvents;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Serban Theodor on 25-Apr-16.
 */
public class Tier4FragmentImage extends Fragment {

    private static String TAG = Tier4FragmentImage.class.getSimpleName();
    private GridView gv;

    private ArrayList<ImageItem> images_ArrayList;
    private ArrayList<String> images_filenameArrayList ;
    private ArrayList<Integer> images_seq_noArrayList;
    private ArrayList<String> images_urlArrayList;
    private ArrayList<String> images_captionArrayList;

    private MyPreferenceManager preferenceManager;
    private boolean textview_visible;
    private ImageView imageView;
    private TextView textView;
    private String directory;
    private int position;
    private int nr_of_images;

    public Tier4FragmentImage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_tier4, container, false);
        preferenceManager = MyApplication.getInstance().getPrefManager();
        directory = preferenceManager.get_directory_for_images_path();

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.hide();


        images_filenameArrayList = new ArrayList<>();
        images_seq_noArrayList = new ArrayList<>();
        images_captionArrayList = new ArrayList<>();
        images_urlArrayList = new ArrayList<>();

        images_ArrayList = new ArrayList<>();

        images_filenameArrayList = getArguments().getStringArrayList("array_of_filename");
        images_seq_noArrayList = getArguments().getIntegerArrayList("array_of_seq_no");
        images_captionArrayList = getArguments().getStringArrayList("array_of_caption");
        //images_urlArrayList = getArguments().getStringArrayList("array_of_url");


        Log.d(TAG, images_filenameArrayList.toString());
        Log.d(TAG, images_seq_noArrayList.toString());

        String nr_of_images_string = getArguments().getString("nr_of_images");
        nr_of_images = Integer.parseInt(nr_of_images_string);
        position = getArguments().getInt("position_of_image");

        textView = (TextView) v.findViewById(R.id.caption_textview);
        textView.setText(images_captionArrayList.get(position));

        imageView = (ImageView) v.findViewById(R.id.image_view_gallery);

        imageView.setImageBitmap(loadImageFromStorage(directory, images_filenameArrayList.get(position)));

        /*
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */

        final GestureDetector gdt = new GestureDetector(new GestureListener());
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });
        return v;
    }

    private Bitmap loadImageFromStorage(String directory_path, String filename)
    {
        Bitmap b = null;
        try {
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

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Right to left
                if(images_seq_noArrayList.get(position) == nr_of_images)
                {
                    return false; // the last image is shown
                }
                else
                {
                    position++;
                    String filename_from_storage = images_filenameArrayList.get(position);
                    Log.d(TAG, filename_from_storage);
                    textView.setText(images_captionArrayList.get(position));
                    imageView.setImageBitmap(loadImageFromStorage(directory, filename_from_storage));
                    return true;
                }
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Left to right
                if(images_seq_noArrayList.get(position) == 1)
                {
                    return false; // the first image is shown
                }
                else
                {
                    position--;
                    String filename_from_storage = images_filenameArrayList.get(position);
                    Log.d(TAG, filename_from_storage);
                    textView.setText(images_captionArrayList.get(position));
                    imageView.setImageBitmap(loadImageFromStorage(directory, filename_from_storage));
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event)
        {
            Log.d(TAG, "on click listener");
            textview_visible = textView.isShown();
            if(textview_visible)
            {
                textView.setVisibility(View.INVISIBLE);
                View decorView = getActivity().getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
            else
            {
                View decorView = getActivity().getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
                textView.setVisibility(View.VISIBLE);
            }
            return false;
        }
    }

}
