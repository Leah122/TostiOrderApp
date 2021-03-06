package com.example.tostiorderappstart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.text.Html;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.security.AccessController.getContext;

public class HelperFunctions extends Activity {

    // used to change the ip everywhere
    public static final String HOST_IP = "192.168.2.20";

    // create a popup with a variable message.
    static void Dialog(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("go back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    // calculate the price of the order
    static void calculatePrice(CheckBox[][] checkboxes, SeekBar seekBar, Button orderBtn) {
        double price = 0;
        for (int i = 0; i <= seekBar.getProgress(); i++) {
            if (checkboxes[i][0].isChecked() || checkboxes[i][1].isChecked()) {
                price += 0.50;

                if (checkboxes[i][0].isChecked() && checkboxes[i][1].isChecked()) {
                    price += 0.10;
                }
            }
        }
        // show price with 2 decimals
        orderBtn.setText("Order: €" +  new DecimalFormat("0.00").format(price));
    }

    // send a get request to the queue
    static String sendGet(String id) {
        String url = "http://" + HelperFunctions.HOST_IP + ":5000/queue?id=" + id;
        Request request = new Request.Builder()
                .url(url)
                .build();

        // because it is http and not https, we need to set the policy to permit all
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

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
    static void startUp(String id, String queue, Context context) {
        if (!queue.equals("-1")) {
            if (!queue.equals("0")) {
                Intent i = new Intent(context, PaymentComplete.class);
                i.putExtra("id", id);
                context.startActivity(i);
            } else if (queue.equals("0")) {
                Dialog("Your order is ready!", context);
            }
        }
    }

    // create a popup with a variable message and go to another activity.
    static void dialogWithIntent(String message, Context context, Class to) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("go back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(context, to);
                        context.startActivity(i);
                    }
                });
        builder.create().show();
    }

    static String setText(Integer amount, Boolean[][] orderList, String name, TextView nameText) {
        String variants = "";
        String text = "You have ordered <font color=#E62272>" + amount.toString() + "</font> toasties: <br>";
        for (int i = 0; i < amount; i++) {
            text += "one toastie with ";
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
        return variants;
    }
}
