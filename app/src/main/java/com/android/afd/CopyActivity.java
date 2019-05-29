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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CopyActivity extends AppCompatActivity {
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
        getSupportActionBar().setTitle("Choose an item to copy:");
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
                String copyTo = data.getExtras().getString("destination");
                File destination = new File(copyTo, source.getName());
                if (destination.getParentFile().isDirectory()) {
                    if (source.isFile()) {
                        try {
                            copyFile(source, destination);
                        } catch (IOException exc) {
                            Intent intent = new Intent();
                            setResult(1, intent);
                            finish();
                            overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
                        }
                    } else {
                        try {
                            copyDirectory(source, destination);
                        } catch (IOException exc) {
                            Intent intent = new Intent();
                            setResult(1, intent);
                            finish();
                            overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
                        }
                    }
                }

                Intent intent = new Intent();
                setResult(1, intent);
                finish();
                overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
                break;
        }
    }

    public static void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
        Log.d(TAG, "Copied file to " + dst.getAbsolutePath());
    }

    private static void copyDirectory(File src, File dst) throws IOException {
        dst.mkdir();
        File[] items = src.listFiles();
        if (items != null && items.length > 0) {
            for (File anItem : items) {
                if (anItem.isDirectory()) {
                    File newDir = new File(dst, anItem.getName());
                    Log.d(TAG, "CREATED DIR: " + newDir.getAbsolutePath());
                    copyDirectory(anItem, newDir);
                } else {
                    File destFile = new File(dst, anItem.getName());
                    copyFile(anItem, destFile);
                }
            }
        }
    }
}
