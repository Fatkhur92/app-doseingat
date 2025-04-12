package com.satyajitghosh.mediclock.medicine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.ArrayList;

/**
 * This class is used for input validation and alerting user about it.
 *
 * @author SATYAJIT GHOSH
 * @since 1.0.0
 */
public class InputValidationHandler {
    /**
     * It validates the user inputs
     *
     * @param medicineName it provides the name of the medicine
     * @param time         it provides the AlarmBundle
     * @return it returns true if the input is fine otherwise false
     */
    public static boolean inputValidation(String medicineName, ArrayList<TIME.AlarmBundle> time) {
        return !medicineName.isEmpty() && !time.isEmpty() && medicineName.length() <= 15;
    }

    /**
     * It is used to show the dialog box to the user , informing him that all the required fields are not filled.
     *
     * @param context it provides the context
     */
    public static void showDialog(Context context) {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(context);

        // Set the message show for the Alert time
        builder.setMessage("Required fields are empty or too large or have special characters");
        // Set Alert Title
        builder.setTitle("MediClock Alert !");

        builder
                .setPositiveButton(
                        "OK",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                dialog.cancel();
                            }
                        });


        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
    }
}
