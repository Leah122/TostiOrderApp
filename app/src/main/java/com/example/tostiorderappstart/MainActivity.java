package com.example.tostiorderappstart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the previously saved id is still in the queue
        SharedPreferences settings = getSharedPreferences("State", 0);
        String id = settings.getString("id", "1");
        id = "1";
        String queue = HelperFunctions.sendGet(id);
        HelperFunctions.startUp(id, queue, MainActivity.this);

        setContentView(R.layout.activity_main);

        // get all the layout components
        Button orderBtn = findViewById(R.id.orderBtn);
        TextInputLayout nameField = findViewById(R.id.nameField);
        SeekBar seekBar = findViewById(R.id.seekBar);
        TableRow row1 = findViewById(R.id.row1);
        TableRow row2 = findViewById(R.id.row2);
        TableRow row3 = findViewById(R.id.row3);

        // make a matrix for the checkboxes
        CheckBox[][] checkboxes = new CheckBox[3][2];
        checkboxes[0][0] = findViewById(R.id.checkBoxHam);
        checkboxes[0][1] = findViewById(R.id.checkBoxCheese);
        checkboxes[1][0] = findViewById(R.id.checkBoxHam2);
        checkboxes[1][1] = findViewById(R.id.checkBoxCheese2);
        checkboxes[2][0] = findViewById(R.id.checkBoxHam3);
        checkboxes[2][1] = findViewById(R.id.checkBoxCheese3);

        // default is 1 tosti so the second and third row of the checkboxes should be gone
        row2.setVisibility(View.GONE);
        row3.setVisibility(View.GONE);

        // set onchange listener for all of the checkboxes to calculate the new price.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                checkboxes[i][j].setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        HelperFunctions.calculatePrice(checkboxes, seekBar, orderBtn);
                    }
                });
            }
        }

        // programming of the order button
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get all values from this page
                String name = nameField.getEditText().getText().toString();
                Integer amount = seekBar.getProgress() + 1; // progress + 1, because it begins at 0

                // make a new list with booleans for the checkboxes to send to the next activity
                Boolean[][] orderList = new Boolean[3][2];
                for (int k = 0; k < 3; k++) {
                    for (int j = 0; j < 2; j++) {
                        orderList[k][j] = checkboxes[k][j].isChecked();
                    }
                }

                // handle errors
                if (name.isEmpty()) {
                    HelperFunctions.Dialog("You have not entered a name.", MainActivity.this);
                } else if ((!(orderList[0][0]) && !(orderList[0][1])) || (!(orderList[1][0]) &&
                        !(orderList[1][1])) || (!(orderList[2][0]) && !(orderList[2][1]))) {
                    HelperFunctions.Dialog("You have ordered a tosti with nothing.", MainActivity.this);
                } else {
                    //go to the next activity with the order.
                    Intent i = new Intent(MainActivity.this, Payment.class);

                    // pass all the values to the next page
                    i.putExtra("name", name);
                    i.putExtra("amount", amount);
                    i.putExtra("orderList", (Serializable) orderList);
                    startActivity(i);
                }
            }
        });


        // showing the value of the seekbar and showing the checkboxes
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // add and remove rows for choosing ham and/or cheese for each tosti (is klein genoeg om het niet dynamisch te genereren)
                if(progress+1 == 1) {
                    row1.setVisibility(View.VISIBLE);
                    row2.setVisibility(View.GONE);
                    row3.setVisibility(View.GONE);
                } else if (progress+1 == 2) {
                    row1.setVisibility(View.VISIBLE);
                    row2.setVisibility(View.VISIBLE);
                    row3.setVisibility(View.GONE);
                } else if (progress+1 == 3) {
                    row1.setVisibility(View.VISIBLE);
                    row2.setVisibility(View.VISIBLE);
                    row3.setVisibility(View.VISIBLE);
                }
                HelperFunctions.calculatePrice(checkboxes, seekBar, orderBtn);
            }

            // these methods have to be here for the OnChangeListener
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }



    // create a popup with a variable message.
    void Dialog (String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message)
                .setPositiveButton("go back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
        return;
    }

    // calculate the price of the order
    void calculatePrice(CheckBox[][] checkboxes, SeekBar seekBar, Button orderBtn) {
        double price = 0;
        for (int i = 0; i <= seekBar.getProgress(); i++) {
            if (checkboxes[i][0].isChecked() || checkboxes[i][1].isChecked()) {
                price += 0.50;

                if (checkboxes[i][0].isChecked() && checkboxes[i][1].isChecked()) {
                    price += 0.10;
                }
            }
        }
        // to make the price with 2 decimals
        DecimalFormat priceDecimal = new DecimalFormat("0.00");
        orderBtn.setText("Order: €" + priceDecimal.format(price));
        return;
    }

    // send a get request to the queue
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

    // determine how to start, depending whether the previous order is in the queue or not
    void startUp(String id, String queue) {
        if (!queue.equals("-1")) {
            if (!queue.equals("0")) {
                Intent i = new Intent(MainActivity.this, PaymentComplete.class);
                i.putExtra("id", id);
                startActivity(i);
            } else if (queue.equals("0")) {
                Dialog("Your order is ready!");
            }
        }
    }
}