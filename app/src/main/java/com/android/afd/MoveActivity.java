package com.android.afd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoveActivity extends AppCompatActivity {
    private static final String TAG = "Testing";

    private List<String> currentFiles;
    private List<String> currentDirectories;

    private File root;
    private File source;


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
        getSupportActionBar().setTitle("Choose an item to move:");
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
                source = new File(root.getAbsoluteFile(), clickedItemName);
                Intent intent = new Intent(getApplicationContext(), FindPathActivity.class);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                Log.d(TAG, "here");
                String moveTo = data.getExtras().getString("destination");
                File destination = new File(moveTo, source.getName());
                if (destination.getParentFile().isDirectory()) {
                  source.renameTo(destination);
                }

                Intent intent = new Intent();
                setResult(1, intent);
                finish();
                overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
                break;
        }
    }
}
