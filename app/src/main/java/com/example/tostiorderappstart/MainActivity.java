package com.example.tostiorderappstart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;


import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView seekBarText = findViewById(R.id.seekBarText);
        Button orderBtn = findViewById(R.id.orderBtn);
        TextInputLayout nameField = findViewById(R.id.nameField);
        SeekBar seekBar = findViewById(R.id.seekBar);
        CheckBox checkBoxHam1 = findViewById(R.id.checkBoxHam);
        CheckBox checkBoxCheese1 = findViewById(R.id.checkBoxCheese);
        CheckBox checkBoxHam2 = findViewById(R.id.checkBoxHam2);
        CheckBox checkBoxCheese2 = findViewById(R.id.checkBoxCheese2);
        CheckBox checkBoxHam3 = findViewById(R.id.checkBoxHam3);
        CheckBox checkBoxCheese3 = findViewById(R.id.checkBoxCheese3);
        TableRow row1 = findViewById(R.id.row1);
        TableRow row2 = findViewById(R.id.row2);
        TableRow row3 = findViewById(R.id.row3);

        // default is 1 tosti so the second and third row should be gone
        row2.setVisibility(View.GONE);
        row3.setVisibility(View.GONE);

        //temporary
        seekBarText.setVisibility(View.GONE);

        // programming of the order button
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Payment.class);

                // get all values from this page
                String name = nameField.getEditText().getText().toString();
                Integer amount = seekBar.getProgress() + 1;
                Boolean ham1 = checkBoxHam1.isChecked();
                Boolean cheese1 = checkBoxCheese1.isChecked();
                Boolean ham2 = checkBoxHam2.isChecked();
                Boolean cheese2 = checkBoxCheese2.isChecked();
                Boolean ham3 = checkBoxHam3.isChecked();
                Boolean cheese3 = checkBoxCheese3.isChecked();


                i.putExtra("name", name);
                i.putExtra("amount", amount);
                i.putExtra("ham1", ham1);
                i.putExtra("cheese1", cheese1);
                if (amount >= 2) {
                    i.putExtra("ham2", ham2);
                    i.putExtra("cheese2", cheese2);
                    if (amount == 3) {
                        i.putExtra("ham3", ham3);
                        i.putExtra("cheese3", cheese3);
                    }
                }

                if (name.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("You have not entered a name.")
                            .setPositiveButton("go back", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //
                                }
                            });
                    // Create the AlertDialog object and return it
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else if ((!ham1 && !cheese1) || (!ham2 && !cheese2) || (!ham3 && !cheese3)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("You have ordered a tosti with nothing.")
                            .setPositiveButton("Go back", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // doei
                                }
                            });
                    AlertDialog dialog = builder.create();

                    dialog.show();

                } else {
                    startActivity(i);
                }

            }
        });

        // showing the value of the seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // set the value under the seekbar to the value of the seekbar (+1 because the seekbar starts at 0)
//                seekBarText.setText(String.valueOf(progress+1));

                // add and remove rows for choosing ham and/or cheese for each tosti (is klein genoeg om het niet dynamisch te genereren
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
}