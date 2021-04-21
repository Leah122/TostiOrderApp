package com.example.tostiorderappstart;

import androidx.appcompat.app.AppCompatActivity;

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

        TextView seekBarText = (TextView) findViewById(R.id.seekBarText);
        Button orderBtn = (Button) findViewById(R.id.orderBtn);
        TextInputLayout nameField = (TextInputLayout) findViewById(R.id.nameField);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        CheckBox checkBoxHam = (CheckBox) findViewById(R.id.checkBoxHam);
        CheckBox checkBoxCheese = (CheckBox) findViewById(R.id.checkBoxCheese);
        CheckBox checkBoxHam2 = (CheckBox) findViewById(R.id.checkBoxHam2);
        CheckBox checkBoxCheese2 = (CheckBox) findViewById(R.id.checkBoxCheese2);
        CheckBox checkBoxHam3 = (CheckBox) findViewById(R.id.checkBoxHam3);
        CheckBox checkBoxCheese3 = (CheckBox) findViewById(R.id.checkBoxCheese3);
        TableRow row1 = (TableRow) findViewById(R.id.row1);
        TableRow row2 = (TableRow) findViewById(R.id.row2);
        TableRow row3 = (TableRow) findViewById(R.id.row3);

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
                Boolean ham = checkBoxHam.isChecked();
                Boolean cheese = checkBoxCheese.isChecked();

                // send the values to the payment page
                // not toevoegen: extra booleans for ham2/3 en cheese2/3
                // nog toevoegen, popup als ham en kaas allebei niet zijn aangevinkt (!ham && !cheese)
                // waarschijnlijk het makkelijkst met een textview met background set en een half
                // doorzichtig iets over het hele scherm om het grijs te maken, dan als je ergens tikt gaat het weg met view.gone
                i.putExtra("name", name);
                i.putExtra("amount", amount);
                i.putExtra("ham1", ham);
                i.putExtra("cheese1", cheese);

                startActivity(i);
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