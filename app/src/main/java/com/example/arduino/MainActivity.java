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
        //PERMISSION checking
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
                JSONObject obj = new JSONObject();
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
                new SendData().execute("http://" + ipadd + "/time", obj.toString());
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
                JSONObject obj1 = new JSONObject();
                try {
                    obj1.put("start", n1);
                    obj1.put("stop", n2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SendData().execute("http://" + ipadd + "/set", obj1.toString());
            }
            });
        }

    //permission
    public static boolean hasPermissions(Context context, String... permissions)
    {
        if (context != null && permissions != null)
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
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}

class SendData extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        String data = "";

        HttpURLConnection httpURLConnection = null;
        try {

            httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            httpURLConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(params[1]);
            wr.flush();
            wr.close();

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
    }
}