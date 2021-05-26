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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;


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

        SharedPreferences settings = getSharedPreferences("State", 0);
        String id = settings.getString("id", "0");
        String queue = sendGet(id);
        if (!queue.equals("-1")) {
            if (!queue.equals("0")) {
                Intent i = new Intent(MainActivity.this, PaymentComplete.class);
                i.putExtra("id", id);
                startActivity(i);
            } else if (queue.equals("0")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Your order is ready!")
                        .setPositiveButton("go back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //niks
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

        setContentView(R.layout.activity_main);

        // get all the layout components
        TextView seekBarText = findViewById(R.id.seekBarText);
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


        // default is 1 tosti so the second and third row should be gone
        row2.setVisibility(View.GONE);
        row3.setVisibility(View.GONE);

        //temporary (changing text under the seekbar, kan waarschijnlijk weg)
        seekBarText.setVisibility(View.GONE);


        // set onchange listener for all of the checkboxes to calculate the price.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                checkboxes[i][j].setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        calculatePrice(checkboxes, seekBar, orderBtn);
                    }
                });
            }
        }

        // programming of the order button
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Payment.class);

                // get all values from this page
                String name = nameField.getEditText().getText().toString();
                Integer amount = seekBar.getProgress() + 1;

                // make a new list with booleans for the checkboxes to send to the next activity
                Boolean[][] orderList = new Boolean[3][2];
                for (int k = 0; k < 3; k++) {
                    for (int j = 0; j < 2; j++) {
                        orderList[k][j] = checkboxes[k][j].isChecked();
                    }
                }

                // pass all the values to the next page
                i.putExtra("name", name);
                i.putExtra("amount", amount);
                i.putExtra("orderList", (Serializable) orderList);

                // handle errors
                if (name.isEmpty()) {
                    Dialog("You have not entered a name.");

                } else if ((!(orderList[0][0]) && !(orderList[0][1])) || (!(orderList[1][0]) &&
                        !(orderList[1][1])) || (!(orderList[2][0]) && !(orderList[2][1]))) {
                    Dialog("You have ordered a tosti with nothing.");

                } else {
                    startActivity(i);
                }
            }
        });

        // showing the value of the seekbar and showing the checkboxes
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // set the value under the seekbar to the value of the seekbar (+1 because the seekbar starts at 0)
//                seekBarText.setText(String.valueOf(progress+1));

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

                calculatePrice(checkboxes, seekBar, orderBtn);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

    }

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
    }



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
}