package com.golchaminerals.golchadash_boardadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;



/**
 * Created by Ashok.Sharma on 13-Sep-17.
 */

public class ParentClass extends AppCompatActivity {
    protected ImageView logout;
    final String TAG = "ParentClass";
    ProgressDialog progressDialog;
    protected void onCreate(Bundle savedInstanceState, int layoutId) {
        super.onCreate(savedInstanceState);
        //Request for custom title bar
        this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //set to your layout file
        setContentView(layoutId);
        //Set the titlebar layout
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_tile_bar);
        ImageView image = (ImageView) findViewById(R.id.logout);
        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Log.i(TAG, "Button Clicked");
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean("LoggedIn", false);
                editor.commit();
                Intent i = new Intent(ParentClass.this, Login.class);
                startActivity(i);
                finish();
            }

        });
    }


    Toast m_currentToast;

    @Override
    protected void onResume() {
        super.onResume();
        if(m_currentToast != null)
        {
            m_currentToast = null;
        }
    }

    void showToast(String text)
    {
        if(m_currentToast != null)
        {
            m_currentToast.cancel();
        }
        m_currentToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        m_currentToast.show();

    }

}

