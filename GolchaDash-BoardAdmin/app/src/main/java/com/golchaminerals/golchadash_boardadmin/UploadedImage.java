package com.golchaminerals.golchadash_boardadmin;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class UploadedImage extends ParentClass {
    ListView list;
    ProgressDialog progressDialog;
    ArrayList<String> imageString = new ArrayList<String>();
    ArrayList<String> imageName = new ArrayList<String>();
    private String TAG = "UploadedImage";
    private FloatingActionButton fab;
    String[] itemname;
    String[] imgid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_uploaded_image);




//        setContentView(R.layout.activity_uploaded_image);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadedImage.this, MainActivity.class);
                startActivity(intent);
//                finish();

            }
        });
        list = (ListView) findViewById(R.id.list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new getUploadedImage().execute();
    }

    void setListView() {
        itemname = new String[imageName.size()];
        imgid = new String[imageString.size()];
        for (int i = 0; i < imageName.size(); i++) {
            itemname[i] = imageName.get(i);
            imgid[i] = imageString.get(i);

            if (i == (imageName.size() - 1)) {
                CustomListAdapter adapter = new
                        CustomListAdapter(UploadedImage.this, itemname, imgid);
                Log.i(TAG, Integer.toString(adapter.getCount()));
                    list.setAdapter(adapter);

//                AdapterView.OnItemClickListener myListViewClicked = new AdapterView.OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                        Toast.makeText(UploadedImage.this, "Clicked at positon = " + position, Toast.LENGTH_SHORT).show();
//
//                    }
//                };
//                list.setOnItemClickListener( myListViewClicked );
            }

        }
    }

    class getUploadedImage extends AsyncTask<Void, Void, Void> {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userName = sharedPrefs.getString("UserName", "nu");
        String passWord2 = sharedPrefs.getString("Password", "np");


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(UploadedImage.this);
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setMessage("Please wait while We are fetching the uploaded images.");
            progressDialog.setCancelable(false);
            progressDialog.show();
            imageName.clear();
            imageString.clear();
        }


        @Override
        protected Void doInBackground(Void... params) {

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://45.114.141.43:1433/Demo Database NAV (9-0);user=" + userName + ";password=" + passWord2);
                Statement stmt = connection.createStatement();
                ResultSet resultSet18 = stmt.executeQuery("SELECT * FROM dbo.ImagesForGolchaDashBoard");
                while (resultSet18.next()) {
                    imageName.add(resultSet18.getString("ImageName"));
                    imageString.add(resultSet18.getString("ImgBase64"));
                }
                connection.close();

            } catch (Exception e) {
                Log.w("Error connection", "" + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            progressDialog.dismiss();
            Log.i(TAG, " Inside On Post Execute");
            progressDialog.dismiss();
            if (imageName != null) {
                if (imageName.size() > 0) {
                    setListView();
                }
            } else {
                Toast.makeText(UploadedImage.this, "There is no images uploaded yet.", Toast.LENGTH_SHORT).show();
            }

        }

    }

}
