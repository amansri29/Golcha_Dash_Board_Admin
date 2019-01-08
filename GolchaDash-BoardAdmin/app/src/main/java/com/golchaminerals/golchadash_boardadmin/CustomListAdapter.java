package com.golchaminerals.golchadash_boardadmin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Ashok.Sharma on 27-Sep-17.
 */

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] imgid;
    private String imageName;
    int i = 1;
    ProgressDialog progressDialog;
    int imagePosition;
    String TAG = CustomListAdapter.class.getSimpleName();

    public CustomListAdapter(Activity context, String[] itemname, String[] imgid) {
        super(context, R.layout.custom_image_layout, itemname);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.itemname = itemname;
        this.imgid = imgid;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        imagePosition = position;
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_image_layout, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        Button delete = (Button) rowView.findViewById(R.id.delete_button);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        txtTitle.setText(itemname[position]);
        byte[] decodedString = Base64.decode(imgid[position], Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
        imageView.setImageBitmap(decodedByte);



//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
//        int imageHeight = options.outHeight;
//        int imageWidth = options.outWidth;
//        String imageType = options.outMimeType;

//        imageView.setImageResource(imgid[position]);
//        extratxt.setText("Description "+itemname[position]);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageName = itemname[position];
                Log.i("Custom List Adapter", imageName);
                new deleteImage().execute();
            }
        });
        return rowView;
    }

    class deleteImage extends AsyncTask<Void, Void, Void> {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sharedPrefs.getString("UserName", "nu");
        String passWord2 = sharedPrefs.getString("Password", "np");

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setMessage("Please wait while We are deleting the images.");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(Void... params) {

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://45.114.141.43:1433/Demo Database NAV (9-0);user=" + userName + ";password=" + passWord2);
                String commands = "DELETE FROM dbo.ImagesForGolchaDashBoard WHERE ImageName = '" + imageName + "'";
                PreparedStatement preStmt = connection.prepareStatement(commands);
                preStmt.executeUpdate();
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
            context.finish();
            context.startActivity(context.getIntent());
        }

    }
}
