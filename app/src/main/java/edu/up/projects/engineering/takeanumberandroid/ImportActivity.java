package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// NICK TESTING SOCKET STUFF
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class ImportActivity extends AppCompatActivity {
    String sessionID;
    int[] layoutParams = new int[4];
    private String content = "";
    private String courseId;
    private String courseSection;
    private String courseName;
    EditText rosterPreview;
    // NICK
    EditText textOut;
    TextView textIn;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button setupB = (Button) findViewById(R.id.setupButton);
        Button queueB = (Button) findViewById(R.id.queueButton);
        Button checkpointsB = (Button) findViewById(R.id.checkpointsButton);
        rosterPreview = (EditText) findViewById(R.id.nameList);
        Button createButton = (Button) findViewById(R.id.createButton);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        Spinner selectCSV = (Spinner) findViewById(R.id.selectCSV);


        setupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(ImportActivity.this,
                        MainActivity.class);
                ImportActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        queueB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(ImportActivity.this,
                        QueueActivity2.class);
                ImportActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        checkpointsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(ImportActivity.this,
                        CheckpointsActivity.class);
                ImportActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });


        try {
            sessionID = getIntent().getExtras().getString("session");
        } catch (NullPointerException notLoadingSession) {
            //TODO generate session ID as though they didn't put in a session id (cause they didn't)
        }

        try {
            layoutParams = getIntent().getExtras().getIntArray("layout");
        } catch (NullPointerException notLoadingSession) {
            //layout params need to come from server in this case
            //TODO get a session id from server in this case - this case should only happen if they selected "load lab session"
        }


        if (sessionID != null) {
            //handler if they input a sessionID
        } else if (layoutParams != null) {
            //handler if they input layout parameters
            //0 = left row, 1 = right row, 2 = left cols, 3 = right cols
        } else {
            //handler for if they chose a preset lab session

        }

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(ImportActivity.this,
                        CheckpointsActivity.class);
                intentMain.putExtra("layout", layoutParams);
                intentMain.putExtra("roster", rosterPreview.getText().toString());
                EditText numChecks = (EditText) findViewById(R.id.numChecks);

                final int numberOfCheckpoints = Integer.parseInt(numChecks.getText().toString());
                intentMain.putExtra("numChecks", numberOfCheckpoints);
                ImportActivity.this.startActivity(intentMain);

                SendfeedbackJob job = new SendfeedbackJob();
                String outMessage = "checkpointInit#";
                String checkpoints = "";
                for (int i = 0; i < numberOfCheckpoints; i++)
                {
                        checkpoints += ",0";
                }

                String labNumber = "01";//TODO lab number hardcoded for now
                String rosterString = content;
                outMessage += courseId + "," + courseSection + "," +labNumber + "," + courseName + "," + numberOfCheckpoints;
                String[] studentNames = rosterString.split("\\n");
                for (String s : studentNames)
                {
                    String[] names = s.split(",");
                    outMessage += "#";
                    outMessage += names[0].trim();
                    outMessage += "," + names[1].trim();
                    outMessage += "," + names[2].trim();
                    outMessage += checkpoints;
                }
                job.execute(outMessage);
            }
        });
        //the list of all files in the project folder
        List<File> spinnerArray = new ArrayList<File>();
        //decided to make the specified folder "Tan" and it should be in the root of the device
        File csvFolder = new File("/sdcard/TAN");
        File[] csvList = csvFolder.listFiles();

        if (csvList != null) {
            ArrayAdapter<File> adapter = new ArrayAdapter<File>(
                    this, android.R.layout.simple_spinner_item, csvList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            final Spinner sItems = (Spinner) findViewById(R.id.selectCSV);
            sItems.setAdapter(adapter);
            sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    File xc = (File) sItems.getSelectedItem();
                    String cont = "";
                    try {
                        cont = readFile(xc.toString());
                    } catch (IOException noFile) {

                    }
                    rosterPreview.setText(cont);


                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
            String selected = sItems.getSelectedItem().toString();
            if (selected.equals("what ever the option was")) {
            }
            File selected2 = (File) selectCSV.getSelectedItem();
            try {
                String[] path = selected2.getPath().split("/");
                String fileName = path[path.length - 1];
                courseId = fileName.split("-")[0].substring(2, 5);
                courseSection = fileName.split("-")[0].substring(5);
                courseName = fileName.split("-")[1].split("\\.")[0];
                content = readFile(selected2.toString());
            } catch (Exception e) {

            }
            rosterPreview.setText(content);
        } else {
            rosterPreview.setText("NO CSV FILES FOUND");
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * A helper method to read a file.
     *
     * @param file - the file to read
     * @return - the string containing the file's contents
     * @throws IOException
     */
    private String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }

    private void setRosterPreview(String roster) {
        rosterPreview.setText(roster);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Import Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.up.projects.engineering.takeanumberandroid/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Import Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.up.projects.engineering.takeanumberandroid/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private class SendfeedbackJob extends AsyncTask<String, Void, String> {
        String message2 = "";
        String outMessage = "";
        PrintWriter out;

        @Override
        protected String doInBackground(String[] params) {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;
            try {
                socket = new Socket("192.168.1.144", 8080);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                outMessage = params[0];
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(outMessage);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String x = "";
            try {
                while (true) {
                    x = "";

                    x = dataInputStream.readLine();
                    System.out.println(x);


                    if (x.equals("ayy")) {
                        dataInputStream.close();
                        dataOutputStream.close();
                        socket.close();
                        out.println("");
                        System.out.println("CLOSING AHHHHHHHHH");
                        break;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return x;
        }

        @Override
        protected void onPostExecute(String message) {
            message2 = message;
        }
    }
}
