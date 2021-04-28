package com.example.tostiorderappstart;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
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

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("Test");
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView nameText = (TextView) findViewById(R.id.paymentName);
        Button payNowBtn = (Button) findViewById(R.id.payNowBtn);
        Button payLaterBtn = (Button) findViewById(R.id.payLaterBtn);

        Bundle extras = getIntent().getExtras();
        String name = null;
        Integer amount = null;
        Boolean ham1;
        Boolean cheese1;
        Boolean ham2;
        Boolean cheese2;
        Boolean ham3;
        Boolean cheese3;

        String variants = "";

        if (extras != null) {
            name = extras.getString("name");
            amount = extras.getInt("amount");
            Boolean[][] orderList = new Boolean[amount][2];

            ham1 = extras.getBoolean("ham1");
            cheese1 = extras.getBoolean("cheese1");
            orderList[0][0] = ham1;
            orderList[0][1] = cheese1;
            if (amount >= 2) {
                ham2 = extras.getBoolean("ham2");
                cheese2 = extras.getBoolean("cheese2");
                orderList[1][0] = ham2;
                orderList[1][1] = cheese2;
                if (amount == 3) {
                    ham3 = extras.getBoolean("ham3");
                    cheese3 = extras.getBoolean("cheese3");
                    orderList[2][0] = ham3;
                    orderList[2][1] = cheese3;
                }
            }


            String text = "You have ordered <font color=#E62272>" + amount.toString() + "</font> tosti's: <br>";
            for (int i = 0; i < amount; i++) {
                text += "one tosti with ";
                if (orderList[i][0] && orderList[i][1]) {
                    text += "ham and cheese <br>";
                    variants += "HamCheese ";
                } else if (orderList[i][0]) {
                    text += "ham <br>";
                    variants += "Ham ";
                } else if (orderList[i][1]) {
                    text += "cheese <br>";
                    variants += "Cheese ";
                }
            }
            text += "with name: <font color=#E62272>" + name + "</font>";
            variants = variants.substring(0, variants.length() - 1);

            nameText.setText(Html.fromHtml(text));

//            if (ham && cheese) {
//                nameText.setText(Html.fromHtml("You have ordered <font color=#E62272>" + amount.toString() +
//                        "</font> tosti(s) with <font color=#E62272>ham and cheese</font>, using the name <font color=#E62272>" + name + "</font>"));
//            } else if (ham) {
//                nameText.setText(Html.fromHtml("You have ordered <font color=#E62272>" + amount.toString() +
//                        "</font> tosti(s) with <font color=#E62272>ham</font>, using the name <font color=#E62272>" + name + "</font>"));
//            } else if (cheese) {
//                nameText.setText(Html.fromHtml("You have ordered <font color=#E62272>" + amount.toString() +
//                        "</font> tosti(s) with <font color=#E62272>cheese</font>, using the name <font color=#E62272>" + name + "</font>"));
//            } else {
//                nameText.setText("You have ordered just toasted bread, please try again ;)");
//            }
        }


        // pay now
        payNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Payment.this, PaymentComplete.class);
                startActivity(i);
            }
        });

        // pay later
        String finalName = name;
        Integer finalAmount = amount;
        String finalVariants = variants;
        payLaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name", finalName)
                        .addFormDataPart("name", finalAmount.toString())
                        .addFormDataPart("variants", finalVariants)
                        .build();

                Request request = new Request.Builder()
                        .url("http://myip/task_manager/v1/register")
                        .post(requestBody)
                        .build();

                final OkHttpClient client = new OkHttpClient();
                try {
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(Payment.this, PaymentComplete.class);
                startActivity(i);
            }
        });
    }
}