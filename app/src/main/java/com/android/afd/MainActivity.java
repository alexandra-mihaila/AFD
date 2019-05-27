package com.android.afd;

import com.android.afd.dialogs.CreateDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "Testing";

    private List<String> currentFiles;
    private List<String> currentDirectories;

    private File root;

    private FloatingActionButton addButton;
    private FloatingActionButton deleteButton;
    private FloatingActionButton renameButton;
    private FloatingActionButton copyButton;
    private FloatingActionButton moveButton;
    private FloatingActionsMenu actionsButton;

    private LinearLayout linearLayout;
    private Integer index = 0;
    private SwipeRefreshLayout pullToRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCurrentDirectory();
                pullToRefresh.setRefreshing(false);
            }
        });


        actionsButton = findViewById(R.id.multiple_actions);
        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionsButton.collapse();
                CreateDialog cd = new CreateDialog();
                cd.displayCreateDialog(MainActivity.this, root);
            }
        });

        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionsButton.collapse();
            }
        });

        copyButton = findViewById(R.id.copy_button);
        copyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionsButton.collapse();
            }
        });

        moveButton = findViewById(R.id.move_button);
        moveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionsButton.collapse();
            }
        });

        renameButton = findViewById(R.id.rename_button);
        renameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionsButton.collapse();
            }
        });

        startApp();
    }

    /**
     * Method for checking WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE permissions
     *
    */
    protected void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // ask for permission
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // ask for permission
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);
        }
    }

    protected void startApp() {
        checkPermissions();

        final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        root = new File(rootPath);

        displayCurrentDirectory(root);
    }

    protected void displayCurrentDirectory(final File currentDirectory) {
        if (!currentDirectory.isDirectory()) {
            return;
        }

        getSupportActionBar().setTitle(currentDirectory.getName());

        getFiles(currentDirectory);
        final List<String> items = new ArrayList<>();
        items.addAll(currentDirectories);
        items.addAll(currentFiles);


        ListView itemsListView = findViewById(R.id.main_list_view);
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
                if (clickedItem.isDirectory()) {
                    root = clickedItem;
                    currentFiles.clear();
                    currentDirectories.clear();
                    displayCurrentDirectory(clickedItem);
                } else {
                }
                actionsButton.collapse();
            }
        });
        itemsListView.setAdapter(arrayAdapter);
    }

    protected void getFiles(File root) {
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

    public void refreshCurrentDirectory() {
        Log.d(TAG, "refresh");
        if (currentDirectories != null)
            currentDirectories.clear();

        if (currentFiles != null)
            currentFiles.clear();

        displayCurrentDirectory(root);
    }

    @Override
    public void onBackPressed(){
        if (root.getAbsolutePath().compareTo("/storage/emulated/0") == 0) {
            Log.d(TAG, "Dead");
            finish();
        } else {
            actionsButton.collapse();
            root = root.getParentFile();
            currentFiles.clear();
            currentDirectories.clear();
            displayCurrentDirectory(root);
        }
    }
}
