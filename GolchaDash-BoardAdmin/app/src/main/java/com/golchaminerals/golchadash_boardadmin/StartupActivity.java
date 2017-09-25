package com.golchaminerals.golchadash_boardadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean loggedIn = sharedPrefs.getBoolean("LoggedIn", false);

        if(loggedIn)
        {
            Intent i = new Intent(StartupActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
            Intent i = new Intent(StartupActivity.this, Login.class);
            startActivity(i);
            finish();
        }
    }
}
