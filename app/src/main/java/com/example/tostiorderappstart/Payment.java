package com.example.tostiorderappstart;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("Test");
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView nameText = (TextView) findViewById(R.id.paymentName);
        Button doneBtn = (Button) findViewById(R.id.doneBtn);

        Bundle extras = getIntent().getExtras();
        String name = "name";
        Integer amount = 0;

        String variants = "";

        if (extras != null) {
            name = extras.getString("name");
            amount = extras.getInt("amount");
            Boolean[][] orderList = new Boolean[amount][2];

            orderList = (Boolean[][]) extras.getSerializable("orderList");


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

        }


        // pay later
                String finalName = name;
                Integer finalAmount = amount;
                String finalVariants = variants;
                doneBtn.setOnClickListener(new View.OnClickListener() {
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