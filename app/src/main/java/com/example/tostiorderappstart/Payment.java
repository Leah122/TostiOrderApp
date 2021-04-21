package com.example.tostiorderappstart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Payment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        TextView nameText = (TextView) findViewById(R.id.paymentName);
        Button payNowBtn = (Button) findViewById(R.id.payNowBtn);
        Button payLaterBtn = (Button) findViewById(R.id.payLaterBtn);

        Bundle extras = getIntent().getExtras();
        String name;
        Integer amount;
        Boolean ham;
        Boolean cheese;


        // show the order


        // moet nog worden:
        // you have ordered the following under the name 'name'
        // for (amount) {- tosti with ham and/or cheese}

        if (extras != null) {
            name = extras.getString("name");
            amount = extras.getInt("amount");
            ham = extras.getBoolean("ham1");
            cheese = extras.getBoolean("cheese1");
            if (ham && cheese) {
                nameText.setText(Html.fromHtml("You have ordered <font color=#E62272>" + amount.toString() +
                        "</font> tosti(s) with <font color=#E62272>ham and cheese</font>, using the name <font color=#E62272>" + name + "</font>"));
            } else if (ham) {
                nameText.setText(Html.fromHtml("You have ordered <font color=#E62272>" + amount.toString() +
                        "</font> tosti(s) with <font color=#E62272>ham</font>, using the name <font color=#E62272>" + name + "</font>"));
            } else if (cheese) {
                nameText.setText(Html.fromHtml("You have ordered <font color=#E62272>" + amount.toString() +
                        "</font> tosti(s) with <font color=#E62272>cheese</font>, using the name <font color=#E62272>" + name + "</font>"));
            } else {
                nameText.setText("You have ordered just toasted bread, please try again ;)");
            }
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
        payLaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Payment.this, PaymentComplete.class);
                startActivity(i);
            }
        });
    }
}