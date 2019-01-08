package com.golchaminerals.golchadash_boardadmin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadImage extends ParentClass {

    public static final int requestcode = 1;
    ImageView img;
    Button btnupload, btnchooseimage;
    EditText edtname;
    byte[] byteArray;
    String encodedImage;
    ProgressBar pg;
    boolean uploadStatus = false;
    Bitmap originBitmap = null;
    private final String TAG = "UploadImage";
    String imageName;
    ProgressDialog progressDialog;
    float dataSize = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_upload_image);
//        setContentView(R.layout.activity_upload_image);
        img = (ImageView) findViewById(R.id.imageview);
        btnupload = (Button) findViewById(R.id.btnupload);
        btnchooseimage = (Button) findViewById(R.id.btnchooseimage);
        edtname = (EditText) findViewById(R.id.edtname);
        pg = (ProgressBar) findViewById(R.id.progressBar1);
        pg.setVisibility(View.GONE);
        btnchooseimage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ChooseImage();
            }
        });
        btnupload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imageName = edtname.getText().toString();
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                Log.i(TAG, "Internet Connectivity " + isConnected);
                if (!isConnected)   // no internet connectivity
                {
                    Toast.makeText(UploadImage.this, "No Internet, Please check your internet connectivity.", Toast.LENGTH_SHORT).show();
                } else {
                    if (imageName.trim().equals("")) {
                        Toast.makeText(UploadImage.this, "Please give a name to image", Toast.LENGTH_SHORT).show();
                    } else {
                        if (originBitmap == null) {
                            Toast.makeText(UploadImage.this, "Please choose an image.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (dataSize <= 1)  {
                                progressDialog = new ProgressDialog(UploadImage.this);
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                progressDialog.setMessage(" Uploading is in progress . . .");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                originBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byteArray = stream.toByteArray();
                                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                                new uploadDataToServer().execute();
                            } else {
                                Toast.makeText(UploadImage.this, "Please select an image which size is less than 1mb.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });

    }


    public void ChooseImage() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)
                && !Environment.getExternalStorageState().equals(
                Environment.MEDIA_CHECKING)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, requestcode);

        } else {
            Toast.makeText(UploadImage.this,
                    "No activity found to perform this task",
                    Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            File f = null;
            Uri selectedImage = data.getData();


            String scheme = selectedImage.getScheme();
            if(scheme.equals(ContentResolver.SCHEME_CONTENT))
            {
                try {
                    InputStream fileInputStream=getApplicationContext().getContentResolver().openInputStream(selectedImage);
                    dataSize = fileInputStream.available();
                    dataSize = dataSize / 1000000;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "File size in bytes " + dataSize);

            }
            else if(scheme.equals(ContentResolver.SCHEME_FILE))
            {
                String path = selectedImage.getPath();
                try {
                    f = new File(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dataSize = f.length() /1000000;
                Log.i(TAG, "File size in bytes " + dataSize);
            }



            InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
                originBitmap = BitmapFactory.decodeStream(imageStream);

            } catch (FileNotFoundException e) {

            }
            if (originBitmap != null) {
                this.img.setImageBitmap(originBitmap);
            }
        } else {
//            txtmsg.setText("There's an error if this code doesn't work, thats all I know");

        }
    }


    public class uploadDataToServer extends AsyncTask<Void, Void, Void> {
        public Connection connection;


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            Calendar cal = Calendar.getInstance();
            Log.i(TAG, "Just Before Getting Date " + cal.getTime().toString());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(cal.getTime());
            Log.i(TAG, "Date " + formattedDate);

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String username = sharedPrefs.getString("UserName", "nu");
                String password = sharedPrefs.getString("Password", "np");
                connection = DriverManager.getConnection("jdbc:jtds:sqlserver://45.114.141.43:1433/Demo Database NAV (9-0);user=" + username + ";password=" + password);
                Log.i(TAG, " Connection Open Now");
//                String commands = "INSERT INTO dbo.ImagesForGolchaDashBoard\n" +
//                        "VALUES ('" + imageName + "','" + encodedImage + "','" + formattedDate + "')";
                String commands = "INSERT INTO dbo.ImagesForGolchaDashBoard\n" +
                        "VALUES ('" + imageName + "','" + encodedImage + "','" + formattedDate + "')";
                // encodedImage which is the Base64 String
                PreparedStatement preStmt = connection.prepareStatement(commands);
                preStmt.executeUpdate();
                Log.i(TAG, "Uploaded Successfully");
                uploadStatus = true;
            } catch (Exception e) {
                Log.w("Error connection", "" + e.getMessage());
                uploadStatus = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (uploadStatus) {
                Toast.makeText(UploadImage.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                uploadStatus = false;
                progressDialog.dismiss();
                Intent intent = new Intent(UploadImage.this, UploadedImage.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(UploadImage.this, "Error, Please try again", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }


    }
}
