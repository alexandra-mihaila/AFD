package com.android.afd.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.android.afd.MainActivity;
import com.android.afd.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateDialog extends AppCompatActivity {
    private String option;
    private EditText nameFile;
    private View view;
    private boolean resultValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void displayCreateDialog(final Activity activity, final File root) {
        final String[] options = {"File", "Folder"};
        AlertDialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.activity_create_dialog, null);
        builder.setTitle("Enter a name:");
        builder.setView(view);
        builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                option = options[i];
            }
        });

        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                nameFile = view.findViewById(R.id.cd_item_name);
                String name = nameFile.getText().toString();
                File newFile = new File(root.getAbsolutePath(), name);

                if (option.compareTo("File") == 0) {
                    try {
                        if (!newFile.createNewFile()) {
                            Log.d("ERROR", "Can't create file");
                        }
                    } catch (java.io.IOException ex) {
                        System.out.println("Something went wrong.");
                    }
                } else if (option.compareTo("Folder") == 0) {
                    if (!newFile.mkdir()) {
                        Log.d("ERROR", "Can't create folder");
                    }
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });


        dialog = builder.create();
        dialog.show();
    }
}