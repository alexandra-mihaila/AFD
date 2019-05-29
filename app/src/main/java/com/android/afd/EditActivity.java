package com.android.afd;

import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import io.github.mthli.knife.KnifeText;


public class EditActivity extends AppCompatActivity {
    private String initialContent;

    private File file;

    private FloatingActionButton done;

    private KnifeText knife;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_file);
        init();
    }

    protected void init() {
        knife = findViewById(R.id.textEditor);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            file = new File(b.getString("file"));
        }

        done = findViewById(R.id.check_edit);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String finalContent = knife.getText().toString();
                try {
                    FileUtils.writeStringToFile(file, finalContent);
                } catch (IOException exc) {

                }
                finish();
            }
        });
        try {
            initialContent = FileUtils.readFileToString(file);
        } catch (IOException exc) {

        }
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(file.getName());
        knife.setText(initialContent);
        knife.setScroller(new Scroller(EditActivity.this));
        knife.setVerticalScrollBarEnabled(true);

    }
}
