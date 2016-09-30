package com.example.serbantheodor.android_hotel;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;


/**
 * Created by Serban Theodor on 19-Feb-16.
 */
public class Tier2Activity extends AppCompatActivity {

    String location_id,parent_id,label, dynamic, page_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tier2_layout);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_view);
        TextView hello = (TextView) actionBar.getCustomView().findViewById(R.id.title);
        hello.setText("Tier 2 Activity");
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM
                | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);

        actionBar.setDisplayHomeAsUpEnabled(true);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            label = extras.getString("label");
            location_id = extras.getString("location_id");
            parent_id = extras.getString("parent_id");
            dynamic = extras.getString("dynamic");
            page_id = extras.getString("page_id");
            Log.d("tier 2","on create extras");
        }


        hello.setText(label);

        Tier2Fragment fragment2 = new Tier2Fragment();

        Bundle bundle = new Bundle();
        bundle.putString("label", label);
        bundle.putString("location_id", location_id);
        bundle.putString("parent_id", parent_id);
        bundle.putString("dynamic", dynamic);
        bundle.putString("page_id", page_id);

        fragment2.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.tier_fragment, fragment2);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
                if (getFragmentManager().getBackStackEntryCount() > 1 ){
                    getFragmentManager().popBackStack();
                } else {
                    this.finish();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
