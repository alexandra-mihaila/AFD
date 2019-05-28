package com.android.afd;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RenameActivity extends AppCompatActivity {
    private final String TAG = "Testing";
    private String clickedItemName;

    private List<String> currentFiles;
    private List<String> currentDirectories;

    private File root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename);
        startActivity();
    }

    protected void init() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            root = new File(b.getString("root"));
        }
    }

    protected void startActivity() {
        init();
        getSupportActionBar().setTitle("Choose an item to rename:");
        displayCurrentDirectory();
    }

    protected void displayCurrentDirectory() {
        getFiles();
        final List<String> items = new ArrayList<>();
        items.addAll(currentDirectories);
        items.addAll(currentFiles);

        final ListView itemsListView = findViewById(R.id.main_list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                items
        );
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedItemName = parent.getItemAtPosition(position).toString();
                final File oldName = new File(root.getAbsoluteFile(), clickedItemName);
                AlertDialog dialog = null;

                AlertDialog.Builder builder = new AlertDialog.Builder(RenameActivity.this);
                LayoutInflater inflater = LayoutInflater.from(RenameActivity.this);
                final View addView = inflater.inflate(R.layout.activity_create_dialog, null);
                builder.setTitle("Enter a new name:");
                builder.setView(addView);

                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText nameFile = addView.findViewById(R.id.item_name);
                        String name = nameFile.getText().toString();
                        File newName = new File(root.getAbsolutePath(), name);
                        oldName.renameTo(newName);
                        dialog.dismiss();
                        finish();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog = builder.create();
                dialog.show();

            }
        });
        itemsListView.setAdapter(arrayAdapter);
    }

    private void getFiles() {
        currentFiles = new ArrayList<>();
        currentDirectories = new ArrayList<>();

        if (root.isDirectory()) {
            for (File item : root.listFiles()) {
                if (item.isDirectory()) {
                    currentDirectories.add(item.getName());
                } else {
                    currentFiles.add(item.getName());
                }
            }
        }

        Collections.sort(currentDirectories);
        Collections.sort(currentFiles);
    }
}
