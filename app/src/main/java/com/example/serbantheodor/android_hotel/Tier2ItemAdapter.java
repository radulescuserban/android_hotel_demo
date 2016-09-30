package com.example.serbantheodor.android_hotel;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Serban Theodor on 13-Feb-16.
 */
public class Tier2ItemAdapter extends BaseAdapter {

    SharedPreferences sharedPreferences;
    private Context context;
    private final ArrayList<String> menu_id;
    private final ArrayList<String> menu_label;
    private final ArrayList<String> menu_icons;
    private final ArrayList<String> menu_page_id;
    private final ArrayList<String> menu_dynamic;
    String location_id;

    public Tier2ItemAdapter(Context context, ArrayList<String> menu_id,ArrayList<String> menu_label,
                            ArrayList<String> menu_icons, ArrayList<String> menu_dynamic,
                            ArrayList<String> menu_page_id,String location_id) {
        this.context = context;
        this.location_id = location_id;
        this.menu_id = menu_id;
        this.menu_label = menu_label;
        this.menu_icons = menu_icons;
        this.menu_page_id = menu_page_id;
        this.menu_dynamic = menu_dynamic;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView = convertView;
        ViewHolder holder;

        //Log.d(TAG, convertView + "");
        if (gridView == null) {

            gridView = inflater.inflate(R.layout.tier2_menu_item, null);
            holder = new ViewHolder();

            Log.d("HomeItemAdapter", menu_label.get(position));
            // set value into textview
            holder.textView = (TextView) gridView
                    .findViewById(R.id.textView);


            // set image based on selected text
            holder.imageView = (ImageView) gridView
                    .findViewById(R.id.imageButton);
            gridView.setTag(holder);

        } else {
            holder = (ViewHolder) gridView.getTag();
            //gridView = (View) convertView;
        }

        holder.textView.setText(menu_label.get(position));

        String uri = "@drawable/"+menu_icons.get(position);

        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        Drawable res = context.getResources().getDrawable(imageResource);
        holder.imageView.setImageDrawable(res);
        return gridView;
    }

    @Override
    public int getCount() {
        return menu_label.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
