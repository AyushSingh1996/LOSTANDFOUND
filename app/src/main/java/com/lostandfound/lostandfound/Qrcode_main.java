package com.lostandfound.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;





import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

//implementing onclicklistener
public class Qrcode_main extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button buttonScan;

    private TextView textViewName, textViewPhoneno;
String Name;
    String Phone;
    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_activity);

        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewPhoneno = (TextView) findViewById(R.id.textPhoneno);

        //intializing scan object
        qrScan = new IntentIntegrator(this);
        Toolbar appbar = (Toolbar) findViewById(R.id.appbar);
        appbar.setTitle("Fill the Details");
        setSupportActionBar(appbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //attaching onclick listener
        buttonScan.setOnClickListener(this);
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    Name=obj.getString("NAME");
                    Phone=obj.getString("PHONE NUMBER");
                    textViewName.setText(obj.getString("NAME"));
                    textViewPhoneno.setText(obj.getString("PHONE NUMBER"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    textViewName.setText(Name);
                    textViewPhoneno.setText(Phone);
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        if(view == buttonScan)
            qrScan.initiateScan();


    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}