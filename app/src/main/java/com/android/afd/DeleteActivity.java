package com.android.afd;

import android.content.Intent;
import android.os.Bundle;
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

public class DeleteActivity extends AppCompatActivity {
    private final String TAG = "Testing";

    private List<String> currentFiles;
    private List<String> currentDirectories;
    private List<String> toDelete = new ArrayList<>();

    private File root;

    private FloatingActionButton checkButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        startActivity();
    }

    protected void init() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            root = new File(b.getString("root"));
        }

        checkButton = findViewById(R.id.check_delete);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (String item: toDelete) {
                    File file = new File(root.getAbsoluteFile(), item);
                    if (!file.delete()) {
                        Log.d(TAG, "not deleted");
                    }
                }
                Intent resultIntent = new Intent();
                setResult(1, resultIntent);
                finish();
                overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
            }
        });
    }

    protected void startActivity() {
        init();
        getSupportActionBar().setTitle("Choose items to delete:");
        displayCurrentDirectory();
    }

    protected void displayCurrentDirectory() {
        getFiles();
        final List<String> items = new ArrayList<>();
        items.addAll(currentDirectories);
        items.addAll(currentFiles);

        final ListView itemsListView = findViewById(R.id.main_list_view);
        itemsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                items
        );
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItemName = parent.getItemAtPosition(position).toString();
                if (!toDelete.contains( clickedItemName )) {
                    itemsListView.setItemChecked(position, true);
                    toDelete.add( clickedItemName );
                } else {
                    itemsListView.setItemChecked(position, false);
                    toDelete.remove( clickedItemName );
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
}
