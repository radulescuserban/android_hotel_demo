package com.example.serbantheodor.android_hotel;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Serban Theodor on 13-Feb-16.
 */
public class HomeItemAdapter extends BaseAdapter {

    private static String TAG = HomeItemAdapter.class.getSimpleName();
    private Context context;
    private final ArrayList<String> menu_id;
    private final ArrayList<String> menu_label;
    private final ArrayList<String> menu_icons;
    private final ArrayList<String> menu_dynamic;
    private final ArrayList<String> page_id;
    String location_id;

    public HomeItemAdapter(Context context, ArrayList<String> menu_id, ArrayList<String> menu_label,
                           ArrayList<String> menu_icons, ArrayList<String> dynamic_menu,
                           ArrayList<String> page_id, String location_id) {
        this.context = context;
        this.location_id = location_id;
        this.menu_id = menu_id;
        this.menu_label = menu_label;
        this.menu_icons = menu_icons;
        this.menu_dynamic = dynamic_menu;
        this.page_id = page_id;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView = convertView;
        ViewHolder holder;

        Log.d(TAG, convertView + "");
        if (gridView == null) {

            gridView = inflater.inflate(R.layout.home_menu_item, null);
            holder = new ViewHolder();

            holder.textView = (TextView) gridView.findViewById(R.id.textView);
            holder.imageView = (ImageView) gridView.findViewById(R.id.imageButton);

            gridView.setTag(holder);

        } else {
            holder = (ViewHolder) gridView.getTag();
        }

        holder.textView.setText(menu_label.get(position));

        String uri = "@drawable/"+menu_icons.get(position);
        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        Drawable res = context.getResources().getDrawable(imageResource);
        holder.imageView.setImageDrawable(res);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Tier2Activity.class);

                Bundle b = new Bundle();
                intent.putExtra("location_id",location_id);
                intent.putExtra("parent_id",menu_id.get(position));
                intent.putExtra("label",menu_label.get(position));
                intent.putExtra("dynamic", menu_dynamic.get(position));
                intent.putExtra("page_id", page_id.get(position));
                intent.putExtras(b);

                v.getContext().startActivity(intent);

            }});

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(
                        v.getContext(),
                        ((TextView) v.findViewById(R.id.textView))
                                .getText(), Toast.LENGTH_SHORT).show();
                //Log.d("in gridview", " click");

                Intent intent = new Intent(context, Tier2Activity.class);
                Bundle b = new Bundle();
                intent.putExtra("location_id",location_id);
                intent.putExtra("parent_id",menu_id.get(position));
                intent.putExtra("label",menu_label.get(position));
                intent.putExtra("dynamic", menu_dynamic.get(position));
                intent.putExtra("page_id", page_id.get(position));
                intent.putExtras(b);
                v.getContext().startActivity(intent);

            }});


        return gridView;
    }

    @Override
    public int getCount() {
        //Log.d(TAG, menu_label.size()+"");
        return menu_label.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
