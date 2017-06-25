package com.cooervo.filmography.controllers.alertdialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.cooervo.filmography.R;


public class NoInternetConnectionDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.no_internet_title))
                .setMessage(context.getString(R.string.no_internet_message))

                .setNegativeButton(R.string.no_internet_negative_btn,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                getActivity().finish();
                            }
                        })
                .setPositiveButton(R.string.no_internet_positive_btn,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent networkSetting = new Intent(
                                        Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(networkSetting);
                            }
                        });

        return builder.create();
    }

}