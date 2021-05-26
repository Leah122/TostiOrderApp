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
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

        TextView orderSentText = findViewById(R.id.orderSentText);
        TextView queueText = findViewById(R.id.queue);
        Button refreshButton = findViewById(R.id.refreshButton);
        orderSentText.setText("Your order has been sent with id: " + id);
        // get request to server with id
        // position in queue weer printen

        String queue = sendGet(id);

        queueText.setText("your place in the queue is: " + queue);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queue = sendGet(id);
                queueText.setText("your place in the queue is: " + queue);

                if (queue.equals("0")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PaymentComplete.this);
                    builder.setMessage("Your order is ready!")
                            .setPositiveButton("go back", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(PaymentComplete.this, MainActivity.class);
                                    startActivity(i);
                                }
                            });
                    // Create the AlertDialog object and return it
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    String sendGet(String id) {
        Request request = new Request.Builder()
                .url("http://192.168.2.20:5000/queue?id=" + id)
                .build();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final OkHttpClient client = new OkHttpClient();

        String queue = "";
        try {
            Response response = client.newCall(request).execute();
            queue = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queue;
    }

    @Override
    public void onBackPressed() {
        // Nothing;
    }

    @Override
    protected void onStop() {
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences("State", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("id", id);

        // Commit the edits!
        editor.commit();
    }
}
