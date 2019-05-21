package com.android.afd;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        test();

    }

    protected void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    protected void test() {
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        File root = new File(fileName);
        getSupportActionBar().setTitle(root.getParentFile().getName());
        List<String> countryList = getFiles(root);
        List<String> countryList2 = getFiles(root);
        countryList.addAll(countryList2);

        ListView simpleList = findViewById(R.id.main_list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                countryList
        );
        simpleList.setAdapter(arrayAdapter);
    }

    protected List<String> getFiles(File root) {
        List<String> items = new ArrayList<>();
        if (root.isDirectory()) {
            for (File aux : root.listFiles()) {
                if (aux.isDirectory()) {
                    items.add(aux.getName());
                } else {
                    items.add(aux.getName());
                }
            }
        }
        return items;
    }
}
