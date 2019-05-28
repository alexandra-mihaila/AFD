package com.android.afd;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "Testing";
    private String addOption;

    private List<String> currentFiles;
    private List<String> currentDirectories;

    private File root;

    private FloatingActionButton addButton;
    private FloatingActionButton deleteButton;
    private FloatingActionButton renameButton;
    private FloatingActionButton copyButton;
    private FloatingActionButton moveButton;
    private FloatingActionsMenu actionsButton;

    private TextView emptyFolder;

    private SwipeRefreshLayout pullToRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    protected void init() {
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCurrentDirectory();
                pullToRefresh.setRefreshing(false);
            }
        });

        emptyFolder = findViewById(R.id.empty);
        actionsButton = findViewById(R.id.multiple_actions);
        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionsButton.collapse();
                final String[] options = {"File", "Folder"};
                AlertDialog dialog = null;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View addView = inflater.inflate(R.layout.activity_create_dialog, null);
                builder.setTitle("Enter a name:");
                builder.setView(addView);
                builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        addOption = options[i];
                    }
                });

                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText nameFile = addView.findViewById(R.id.item_name);
                        String name = nameFile.getText().toString();
                        File newFile = new File(root.getAbsolutePath(), name);

                        if (addOption.compareTo("File") == 0) {
                            try {
                                if (!newFile.createNewFile()) {
                                    Log.d("ERROR", "Can't create file");
                                }
                            } catch (java.io.IOException ex) {
                                System.out.println("Something went wrong.");
                            }
                        } else if (addOption.compareTo("Folder") == 0) {
                            if (!newFile.mkdir()) {
                                Log.d("ERROR", "Can't create folder");
                            }
                        }
                        dialog.dismiss();
                        refreshCurrentDirectory();
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
        });

        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionsButton.collapse();
                Intent intent = new Intent(getApplicationContext(), DeleteActivity.class);
                Bundle b = new Bundle();
                b.putString("root", root.getAbsolutePath());
                intent.putExtras(b);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.slide_up,  R.anim.no_animation);
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
                Intent intent = new Intent(getApplicationContext(), RenameActivity.class);
                Bundle b = new Bundle();
                b.putString("root", root.getAbsolutePath());
                intent.putExtras(b);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.slide_up,  R.anim.no_animation);
            }
        });

        final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        root = new File(rootPath);
    }

    protected void startApp() {
        checkPermissions();
        init();
        displayCurrentDirectory(root);
    }

    protected void displayCurrentDirectory(File currentDirectory) {
        if (currentDirectory.getName().compareTo("0") == 0) {
            getSupportActionBar().setTitle("Current directory: internal storage (root)");
        } else {
            getSupportActionBar().setTitle("Current directory: " + currentDirectory.getName());
        }

        getFiles(currentDirectory);
        final List<String> items = new ArrayList<>();
        items.addAll(currentDirectories);
        items.addAll(currentFiles);
        if (items.size() == 0) {
            emptyFolder.setVisibility(View.VISIBLE);
        } else {
            emptyFolder.setVisibility(View.GONE);
        }

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
                    // open file
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
            refreshCurrentDirectory();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                refreshCurrentDirectory();
                break;
        }
    }
}
