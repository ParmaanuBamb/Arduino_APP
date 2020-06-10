package com.example.arduino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.net.Proxy.Type.HTTP;

public class MainActivity extends AppCompatActivity {
    EditText ip, int1, int2;
    Button save, submit, button;
    String ipadd, n1, n2;
    URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //PERMISSION
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        //checking connection
        if (isNetwork(getApplicationContext())){

            Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(getApplicationContext(), "Internet Is Not Connected", Toast.LENGTH_SHORT).show();
        }
        ip = (EditText) findViewById(R.id.ip);
        int1 = (EditText) findViewById(R.id.int1);
        int2 = (EditText) findViewById(R.id.int2);
        save = (Button) findViewById(R.id.save);
        submit = (Button) findViewById(R.id.submit);
        button = (Button) findViewById(R.id.button);

        //save button for ip
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipadd = ip.getText().toString();
            }
        });

        //update button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting time when button clicked
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm.ss");
                String output = dateFormat.format(currentTime);
                String h = output.substring(0, 2);
                String m = output.substring(3, 5);
                String s = output.substring(6, 8);
                //JSON
                JSONObject obj = null;
                try {
                    obj.put("hour", h);
                    obj.put("minute", m);
                    obj.put("second", s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    url = new URL("http://" + ipadd + "/time");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                postData(url, obj);
            }
        });


        //submit button for integers
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //integers to json
                n1 = int1.getText().toString();
                n2 = int2.getText().toString();
                JSONObject o1 = null;
                try {
                    o1.put("integer 1", n1);
                    o1.put("integer 2", n2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                postData(url, o1);
            }
            });
        }

    public void postData(URL url, JSONObject obj) {
        // Creating a new HttpClient and Post Header
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        DataOutputStream os = null;
        try {
            os = new DataOutputStream(conn.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.writeBytes(obj.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //permission
    public static boolean hasPermissions(Context context, String... permissions)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }

    //connection check
    public boolean isNetwork(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}