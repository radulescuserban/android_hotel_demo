package com.example.serbantheodor.android_hotel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Serban Theodor on 22-Feb-16.
 */
public class CheckErrorActivity extends AppCompatActivity {
    TextView error_text;
    Button retry_con_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_error);

        String error_type = getIntent().getStringExtra("error_type");

        error_text = (TextView) findViewById(R.id.error_text);
        retry_con_button = (Button) findViewById(R.id.retry_con_button);

        if(error_type.equals("unregistered_device"))
            error_text.setText(R.string.error_message_unregistered_device);
        else
            error_text.setText(R.string.error_message);

        retry_con_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Error ", "click");
                Intent intent = new Intent(CheckErrorActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
