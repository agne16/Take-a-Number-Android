package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * QUICK REF for messages tablet sends/expects to receive:
 * message sent for requesting positions: getqueue#sessionId
 * Message expected from server: positions#first,last,id,position,queuenum#...
 * message sent for removing a student from queue: leavequeue#sessionid#id
 */
public class QueueActivity2 extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener
{
    public static int[] layout;
    public static String sessionID;
    public static Hashtable<String, Button> positions;
    public static Button[] posits;
    WebSocket client = null;
    boolean testing = MainActivity.isTesting;
    int[] layoutParams;
    private static final String TAG = "QueueActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (layout == null)
        {
            //set a default layout that's all empty
            //as a reminder, 0 = left rows, 1 = right rows, 2 = left columns, 3 = right columns
            layout = new int[4];
            layout[0] = 5;
            layout[1] = 4;
            layout[2] = 5;
            layout[3] = 3;
        }

        if (sessionID == null)
        {
            try
            {
                sessionID = getIntent().getExtras().getString("session");
            }
            catch (Exception sessionIDNotSet)
            {
                sessionID = "NO SESSION ID FOUND, PLEASE TRY AGAIN NEXT YEAR";
            }
        }


        LinearLayout leftRows = (LinearLayout) findViewById(R.id.leftRows);
        LinearLayout rightRows = (LinearLayout) findViewById(R.id.rightRows);

        TextView sessionTextView = (TextView) findViewById(R.id.sessionText);
        sessionTextView.setText("Session ID: " + sessionID);

        //create buttons for left row
        int totalLeftRows = layout[0];
        int totalLeftColumns = layout[1];
        int totalRightRows = layout[2];
        int totalRightColumns = layout[3];

        if (positions == null)
        {
            positions = new Hashtable<String, Button>();
        }
        //two loops to set up the layout of the classroom
        for (int currentRow = 0; currentRow < totalLeftRows; currentRow++)
        {
            LinearLayout oneRow = new LinearLayout(this);
            oneRow.setOrientation(LinearLayout.HORIZONTAL);
            for (int currentColumn = 0; currentColumn < totalLeftColumns; currentColumn++)
            {
                //these two are if we decide to start at 1 instead of 0 for col/row indices
                int colId = currentColumn + 1;
                int rowId = currentRow + 1;

                String id = "c" + currentColumn + "r" + currentRow;
                Log.i(TAG, "Seat created: " + id);
                Button onePosition = new Button(this);
                onePosition.setOnClickListener(this);
                onePosition.setWidth(200);
                onePosition.setHeight(200);
                LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //left, top, right, bottom margins
                par.setMargins(0, 5, 5, 5);
                onePosition.setLayoutParams(par);
                onePosition.setOnLongClickListener(this);
                if (positions.get(id) == null)//means no one is sitting there
                {
                    onePosition.setText("EMPTY");
                    positions.put(id, onePosition);
                }
                else
                {
                    String name = positions.get(id).getText().toString();
                    onePosition.setText(name);
                    String[] info = name.split(" ");
                    if(info.length==4){
                        if(info[3].equals("1")){
                            onePosition.setBackgroundColor(Color.BLUE);
                        }
                        if(info[3].equals("2")){
                            onePosition.setBackgroundColor(Color.CYAN);
                        }
                    }
                    //not sure if need to overwrite
                    positions.put(id, onePosition);
                }
                oneRow.addView(onePosition);
            }
            leftRows.addView(oneRow);

        }
        boolean foundStuff = false;
        for (int currentRow = 0; currentRow < totalRightRows; currentRow++)
        {
            LinearLayout oneRow = new LinearLayout(this);
            oneRow.setOrientation(LinearLayout.HORIZONTAL);
            for (int currentColumn = 0; currentColumn < totalRightColumns; currentColumn++)
            {
                int colIdOffset = currentColumn + totalLeftColumns;
                int rowId = currentRow;

                String id = "c" + colIdOffset + "r" + currentRow;
                Log.i(TAG, "Seat created: " + id);
                Button onePosition = new Button(this);
                onePosition.setOnClickListener(this);
                onePosition.setWidth(200);
                onePosition.setHeight(200);
                LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //left, top, right, bottom margins
                par.setMargins(0, 5, 5, 5);
                onePosition.setLayoutParams(par);
                onePosition.setOnLongClickListener(this);
                if (positions.get(id) == null)
                {
                    onePosition.setText("EMPTY");
                    positions.put(id, onePosition);
                }
                else
                {
                    String name = positions.get(id).getText().toString();
                    onePosition.setText(name);
                    foundStuff = true;
                    String[] info = name.split(" ");
                    if(info.length==4){
                        if(info[3].equals("1")){
                            onePosition.setBackgroundColor(Color.BLUE);
                        }
                        if(info[3].equals("2")){
                            onePosition.setBackgroundColor(Color.CYAN);
                        }
                    }
                    //not sure if need to overwrite
                    positions.put(id, onePosition);
                }
                oneRow.addView(onePosition);
            }
            rightRows.addView(oneRow);

        }

        if(!foundStuff && !testing){
            //positions = new Hashtable<String,Button>();
            //need to get student positions from server
            networkRequest("getpositions#" + sessionID);
            String serverResponse = getServerResponse();
            System.out.println(serverResponse + "AYYY");
            //updateQueue(serverResponse);
        }
        else if(!foundStuff && testing){
            //format is messageType#firstname,lastname,position,queueNum#...
            updateQueue("POSITIONS#first,last,last16,c1r1,0#first,last,last216,c1r2,1");
        }
        else if(!testing)
        {
            networkRequest("getpositions#" + sessionID);
            String serverResponse = getServerResponse();
            //updateQueue(serverResponse);
        }

        Button setupB = (Button) findViewById(R.id.setupButton);
        Button queueB = (Button) findViewById(R.id.queueButton);
        Button checkpointsB = (Button) findViewById(R.id.checkpointsButton);


        setupB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentMain = new Intent(QueueActivity2.this,
                        MainActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                QueueActivity2.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        queueB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });

        checkpointsB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentMain = new Intent(QueueActivity2.this,
                        CheckpointsActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                QueueActivity2.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        Button queueSync = (Button) findViewById(R.id.queueSync);
        queueSync.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String toSend = "getpositions#" + sessionID;
                String serverResponse = "";
                if (!testing)
                {
                    networkRequest(toSend);
                    serverResponse = getServerResponse();
                }
                else
                {
                    serverResponse = "QUEUEPOSITIONS#last16,first,last,c1r1,0#fennekin16,fennekin,fennekin,c3r3,1";
                }

                //updateQueue(serverResponse);

            }
        });


    }


    /**
     * updateQueue -
     * helper method to update the queue UI after a response from the server
     * used to:
     * get positions of students in the room
     * get queue positions of students in the room
     *
     * @param response - the response from the server
     */
    public void updateQueue(String response)
    {
        if (response.equals("positions")){
            return;
        }
       // positions#first,last,id,position,queuenum#...
        //first, clear out the current queue to make way for the new one
        Enumeration<Button> buttons = positions.elements();
        positions.get("c0r0").setText("EMPTY");//need to do first one manually because enumeration can't get it
        positions.get("c0r0").setBackgroundColor(Color.GRAY);
        while(buttons.hasMoreElements()){
            Button butt = buttons.nextElement();
            butt.setBackgroundColor(Color.GRAY);
            butt.setText("EMPTY");
        }

        String[] initSplit = response.split("#");
        for (int currentName = 1; currentName < initSplit.length; currentName++)
        {
            //first,last,c1r1,0
            String[] oneStudent = initSplit[currentName].split(",");
            String id = oneStudent[3];//the student's position
            int queuePosition = Integer.parseInt(oneStudent[4]);
            //POSITIONS#first,last,c1r1,0#first,last,c132,1 - the format of the response, supposedly
            positions.get(id).setText(oneStudent[1] + " " + oneStudent[2] + " " + oneStudent[0]);
            switch (queuePosition)
            {
                case 0:
                    //means not in queue
                    positions.get(id).setBackgroundColor(Color.GRAY);
                    break;
                case 1:
                    //means first in queue
                    positions.get(id).setBackgroundColor(Color.BLUE);
                    positions.get(id).setText(positions.get(id).getText().toString() + " 1");
                    break;
                case 2:
                    //second, duh
                    positions.get(id).setBackgroundColor(Color.CYAN);
                    positions.get(id).setText(positions.get(id).getText().toString() + " 2");
                    break;
                default:
                    //3+ in queue
                    positions.get(id).setBackgroundColor(Color.GRAY);
                    positions.get(id).setText(positions.get(id).getText().toString() + queuePosition);
                    break;
            }
        }

    }

    public void removeFromQueue(Button butt)
    {
        String UPid = butt.getText().toString().split(" ")[2];
        String toSend = "leavequeue#" + sessionID + "#" + UPid;
        if(!testing){
            networkRequest(toSend);
        }
        System.out.println(toSend);

    }

    public void networkRequest(String message)
    {
        String toSend = message;
        if (this.client == null)
        {
            this.client = WebSocketHandler.getWebSocket();
        }

        client.send(toSend);
    }

    public String getServerResponse()
    {
        String response = client.getLastMessage();
        while (response.equals(""))
        {
            response = client.getLastMessage();
        }
        Log.i(TAG, "Message received: " + response);
        updateQueue(response);
        return "";
    }


    @Override
    public void onClick(View v)
    {
        //NOTE - SHOULD ONLY BE USED FOR QUEUE BUTTONS
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(!MainActivity.isTesting) {

            this.client = WebSocketHandler.getWebSocket();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        while (!client.needQueueUpdate) {
                            try
                            {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateQueue(client.getLastMessage());
                            }
                        });
                        client.needQueueUpdate = false;
                    }
                }
            }).start();

        }
        Log.i(TAG, "onResume reached");
    }

    @Override
    public boolean onLongClick(View v) {
        System.out.println("LONGCLICKED");
        if(v instanceof Button){
            Button x = (Button) v;
            removeFromQueue(x);
        }
        client.send("getPositions#" + sessionID);
        return true;
    }
}
