package com.example.tostiorderappstart;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Payment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        
        // creates the upper actionbar containing a back button
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("Go Back");
        actionBar.setDisplayHomeAsUpEnabled(true);

        // get all components of this view
        TextView nameText = (TextView) findViewById(R.id.paymentName);
        Button doneBtn = (Button) findViewById(R.id.doneBtn);

        // initialize some variables
        String name = "name";
        Integer amount = 0;
        String variants = "";
        
        // get intent from home page
        Bundle extras = getIntent().getExtras();
        
        if (extras != null) {
            name = extras.getString("name");
            amount = extras.getInt("amount");
            Boolean[][] orderList = new Boolean[amount][2];
            orderList = (Boolean[][]) extras.getSerializable("orderList");

            // set the text of the order
            variants = HelperFunctions.setText(amount, orderList, name, nameText);
        }

        // had to be final for the HTTP request
        final String finalName = name;
        final Integer finalAmount = amount;
        final String finalVariants = variants;

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // creating the POST request
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name", finalName)
                        .addFormDataPart("amount", finalAmount.toString())
                        .addFormDataPart("variants", finalVariants)
                        .build();
                Request request = new Request.Builder()
                        .url("http://" + HelperFunctions.HOST_IP + ":5000/tosti")
                        .post(requestBody)
                        .build();

                // because it is http and not https, we need to set the policy to permit all
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

                // make a client
                final OkHttpClient client = new OkHttpClient();

                // try to send the request and get the response, if something went wrong, t
                String id = "";
                try {
                    Response response = client.newCall(request).execute();
                    id = response.body().string();

                } catch (IOException e) {
                    e.printStackTrace();
                    HelperFunctions.dialogWithIntent("Something went wrong, please try again",Payment.this, MainActivity.class);
                }

                //go to the payment complete page
                Intent i = new Intent(Payment.this, PaymentComplete.class);
                i.putExtra("id", id);
                startActivity(i);
            }
        });
    }
}
