package com.just_a_jason.welcomer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.helloworld.HelloWorld;

public class Welcomer {

    public static void welcomeNewPlayer(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
            .setTitle(HelloWorld.hello())
            .setMessage(
                "The game has been modified by @Just-a-Jason on github.com."
            )

            .setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }
            );

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
