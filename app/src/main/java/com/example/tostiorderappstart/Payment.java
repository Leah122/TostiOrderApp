package com.example.tostiorderappstart;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
        
        // weet ff niet wat dit is
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("Test");
        actionBar.setDisplayHomeAsUpEnabled(true);

        // get all components of this view
        TextView nameText = (TextView) findViewById(R.id.paymentName);
        Button doneBtn = (Button) findViewById(R.id.doneBtn);

        // initialize some variables
        String name = "name";
        Integer amount = 0;
        String variants = "";
        
        // get intent from home page
        Bundle extras = getIntent().getExtras();
        
        if (extras != null) {
            name = extras.getString("name");
            amount = extras.getInt("amount");
            Boolean[][] orderList = new Boolean[amount][2];
            orderList = (Boolean[][]) extras.getSerializable("orderList");

            // set the text of the order
            HelperFunctions.setText(amount, orderList, name, nameText);
        }

        // had to be final for the HTTP request
        String finalName = name;
        Integer finalAmount = amount;
        String finalVariants = variants;
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name", finalName)
                        .addFormDataPart("amount", finalAmount.toString())
                        .addFormDataPart("variants", finalVariants)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.2.20:5000/tosti")
                        .post(requestBody)
                        .build();

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                final OkHttpClient client = new OkHttpClient();

                String id = "";
                try {
                    Response response = client.newCall(request).execute();
                    id = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();

                    HelperFunctions.dialogWithIntent("Something went wrong, please try again",Payment.this, MainActivity.class);
//                    // dit moet nog met een dialog call!!
//                    AlertDialog.Builder builder = new AlertDialog.Builder(Payment.this);
//                    builder.setMessage("Something went wrong ;(")
//                            .setPositiveButton("go back", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Intent i = new Intent(Payment.this, MainActivity.class);
//                                    startActivity(i);
//                                }
//                            });
//                    // Create the AlertDialog object and return it
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
                }

                Intent i = new Intent(Payment.this, PaymentComplete.class);
                i.putExtra("id", id);
                startActivity(i);
            }
        });
    }
    
    void setText(Integer amount, Boolean[][] orderList, String name, TextView nameText) {
        String variants = "";
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
    
    
}