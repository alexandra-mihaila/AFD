package com.android.afd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindPathActivity extends AppCompatActivity {
    private final String TAG = "Testing";

    private List<String> currentFiles;
    private List<String> currentDirectories;

    private File root;

    private FloatingActionButton selectButton;

    private boolean selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        startActivity();
    }

    protected void init() {
        final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        root = new File(rootPath);

        selectButton = findViewById(R.id.check_delete);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setTitle("Choose the folder:");
                selected = true;
            }
        });
    }

    protected void startActivity() {
        init();
        getSupportActionBar().setTitle("Choose destination:");
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
                String clickedItemName = parent.getItemAtPosition(position).toString();
                File clickedItem = new File(root.getAbsolutePath(), clickedItemName);
                if (selected) {
                    Log.d(TAG, "find_act " + clickedItem.getAbsolutePath());
                    Intent intent = new Intent();
                    intent.putExtra("destination", clickedItem.getAbsolutePath());
                    setResult(2, intent);
                    finish();
                    overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
                } else {
                    if (clickedItem.isDirectory()) {
                        root = clickedItem;
                        currentFiles.clear();
                        currentDirectories.clear();
                        displayCurrentDirectory();
                    } else {
                        // open file
                    }
                }
            }
        });
        itemsListView.setAdapter(arrayAdapter);
    }

    protected void getFiles() {
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

    @Override
    public void onBackPressed(){
        if (root.getAbsolutePath().compareTo("/storage/emulated/0") == 0) {
            Log.d(TAG, "Dead");
            finish();
        } else {
            root = root.getParentFile();
            currentDirectories.clear();
            currentFiles.clear();
            displayCurrentDirectory();
        }
    }
}
