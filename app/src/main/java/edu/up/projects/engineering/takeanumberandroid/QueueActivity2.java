package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class QueueActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button setupB = (Button) findViewById(R.id.setupButton);
        Button queueB = (Button) findViewById(R.id.queueButton);
        Button checkpointsB = (Button) findViewById(R.id.checkpointsButton);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        
          /*
         * AYYYY for these to work, ya gotsta set whatever data you need for the page or something.
         */
        setupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(QueueActivity2.this,
                        SetupActivity.class);
                QueueActivity2.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        queueB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        checkpointsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(QueueActivity2.this,
                        CheckpointsActivity.class);
                QueueActivity2.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });
    }

}
