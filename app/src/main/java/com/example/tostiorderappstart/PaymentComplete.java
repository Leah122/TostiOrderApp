package com.example.tostiorderappstart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PaymentComplete extends AppCompatActivity {

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_complete);

        // get intent from the previous page
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

        // get all the layout components
        TextView orderSentText = findViewById(R.id.orderSentText);
        TextView queueText = findViewById(R.id.queue);
        Button refreshButton = findViewById(R.id.refreshButton);
        orderSentText.setText("Your order has been sent with id: " + id);

        // send GET request to the server to get the place in the queue
        String queue = HelperFunctions.sendGet(id);
        queueText.setText("your place in the queue is: " + queue);

        // set the onclick for the refresh button
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queue = HelperFunctions.sendGet(id);
                queueText.setText("your place in the queue is: " + queue);

                // if you are removed from the queue, start the main page to order again
                if (queue.equals("0") || queue.equals("-1")) {
                    startActivity(new Intent(PaymentComplete.this, MainActivity.class));
                }
            }
        });
    }

    // the back button should not work in this activity
    @Override
    public void onBackPressed() {

    }

    // when the app is stopped in this activity, the id is saved in the sharedpreferences.
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences("State", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("id", id);
        editor.commit();
    }
}
