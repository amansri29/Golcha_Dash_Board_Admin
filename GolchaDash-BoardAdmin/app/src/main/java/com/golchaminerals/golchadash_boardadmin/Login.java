package com.golchaminerals.golchadash_boardadmin;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;

import static java.security.AccessController.getContext;

public class Login extends ParentClass {
    ProgressDialog progressDialog;
    EditText emaiId, password;
    String userName;
    String passWord2;
    Button login;
    boolean connectionFailed = false;
    final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_login);
        login = (Button) findViewById(R.id.btnLogin);
        emaiId = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        emaiId.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        ImageView imageView = (ImageView) findViewById(R.id.logout);
        imageView.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPrefs.getString("UserName", "nu");
        if (!userName.equals("nu")) {
            emaiId.setText(userName);
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProcess();
            }
        });

    }

    void loginProcess() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        Log.i(TAG, "Internet Connectivity " + isConnected);
        if (!isConnected)   // no internet connectivity
        {
            Toast.makeText(Login.this, "No Internet, Please check your internet connectivity.", Toast.LENGTH_SHORT).show();
        } else {
            userName = emaiId.getText().toString();
            passWord2 = password.getText().toString();
            if (userName.trim().equals("") || passWord2.trim().equals("")) {
                Toast.makeText(Login.this, "Please complete the field", Toast.LENGTH_SHORT).show();
            } else {
                new getLoginDetails().execute();
            }
        }
    }


    class getLoginDetails extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            connectionFailed = false;
            progressDialog = new ProgressDialog(Login.this);
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setMessage("Please wait while We are authenticating You.");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(Void... params) {

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://45.114.141.43:1433;user=" + userName + ";password=" + passWord2);
                Log.i("Connection  Login", " Connection Open Now");
                if (connection == null) {
                    connectionFailed = true;
                } else {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("LoggedIn", true);
                    editor.putString("UserName", userName);
                    editor.putString("Password", passWord2);
                    editor.commit();
                    Intent i = new Intent(Login.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                connection.close();

            } catch (Exception e) {
                Log.w("Error connection", "" + e.getMessage());
                connectionFailed = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (connectionFailed) {
                Toast.makeText(Login.this, "Invalid Credentials, Please try again.", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            Log.i(TAG, " Inside On Post Execute");
        }


    }

}