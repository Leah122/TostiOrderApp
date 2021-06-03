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
        String id = settings.getString("id", "0");
        if (!id.equals("0")) {
            String queue = HelperFunctions.sendGet(id);
            HelperFunctions.startUp(id, queue, MainActivity.this);
        }

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
        for (CheckBox[] checkboxList : checkboxes) {
            for (CheckBox checkbox : checkboxList) {
                checkbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
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
                if (name.isEmpty()) { // no name
                    HelperFunctions.Dialog("You have not entered a name.", MainActivity.this);
                } else if (!(orderList[0][0] || orderList[0][1]) || !(orderList[1][0] || // one tosti with nothing is ordered
                        orderList[1][1]) || !(orderList[2][0] || orderList[2][1])) {
                    HelperFunctions.Dialog("You have ordered a toastie with nothing.", MainActivity.this);
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

        // showing the checkboxes and calculating the price when the seekbar changes
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // add and remove rows for choosing ham and/or cheese for each tosti (is klein genoeg om het niet dynamisch te genereren)
                if(progress == 0) {
                    row1.setVisibility(View.VISIBLE);
                    row2.setVisibility(View.GONE);
                    row3.setVisibility(View.GONE);
                } else if (progress == 1) {
                    row1.setVisibility(View.VISIBLE);
                    row2.setVisibility(View.VISIBLE);
                    row3.setVisibility(View.GONE);
                } else if (progress == 2) {
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
}
