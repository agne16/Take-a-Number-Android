package edu.up.projects.engineering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * based on http://cs.lmu.edu/~ray/notes/javanetexamples/
 * A Capitalization Server and Client
 */
public class Server
{
    private String rosterCsvFilePath;
    private String savedLabSessionFilePath;
    private LabState currentLabState;
    private boolean running;

    /**
     * Called from ServerMain.main() as a new server instance
     *
     * @throws IOException
     */
    public void runServer() throws IOException
    {
        //report ip address in the command line
        InetAddress ip;
        try
        {
            ip = InetAddress.getLocalHost();
            System.out.println("Server running on IP address: " + ip.toString().split("/")[1]);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }



        //create an always listening server
        int clientNumber = 0;   // increments every time a new client connects
        running = true;
        int port = 8081;
        ServerSocket listener = new ServerSocket(port);
        try
        {
            System.out.println("Running on port: " + port);
            while (running)
            {
                //create a new instance of a class that reads a network input
                new Writer(listener.accept(), clientNumber++).start();
            }
        }
        finally
        {
            listener.close();
        }
    }

    private class Writer extends Thread
    {
        private Socket socket;
        private int clientNumber;

        public Writer(Socket socket, int clientNumber)
        {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }

        public void run()
        {
            try
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Hello, you are client #" + clientNumber + ".");
                out.println("Enter a line with only a period to quit\n");

                while (true)
                {
                    //read an interpret any incoming messages
                    String input = in.readLine().trim().toLowerCase();
                    if (input.equals("") || input.equals("."))
                    {
                        break;  //closes connection with a client. server is still running
                    }

                    //my way of testing out different ways of invoking methods
                    //depending on the message sent
                    else if(input.equals("append"))
                    {
                        out.println("Invoking 'append' command");
                    }
                    else if(input.equals("do something"))
                    {
                        out.println("Invoking 'doing something' command");

                        String rootPath = System.getProperty("user.dir");   //root of project folder
                        String filename = "CS273-A-ComputerScienceLaboratory-17378.xml"; // filename

                        // write a sample xml file
                        XMLHelper helper = new XMLHelper();
                        helper.writeFile();

                        // parse a sample xml file to an object and print values
                        LabState labState = helper.parseXML(rootPath, filename);
                        System.out.println("Lab Session ID: " + labState.getSessionId());
                        System.out.println("Student: " + labState.getClassRoster()[0]);
                        System.out.println("Checkpoint 1: " + labState.getCheckpoints()[0][1]);
                    }
                    else
                    {
                        out.println("nothing doing");
                    }
                    //out.println(input.toUpperCase());
                }
            }
            catch (IOException e)
            {
                log("Error handling client# " + clientNumber + ": " + e);
            }
            finally
            {
                try
                {
                    socket.close();
                }
                catch (IOException e)
                {
                    log("Couldn't close a socket, what's going on?");
                }
                log("Connection with client# " + clientNumber + " closed");
            }
        }

        private void log(String message) {
            System.out.println(message);
        }
    }


    /**
     *
     * Create a LabState object given a LabSession object
     *
     * @param labSession - the LabSession object to convert
     * @return the LabState
     */
    public LabState initLabState(LabSession labSession)
    {
        String sessionId = labSession.getSessionId();
        int numCheckpoints = labSession.getNumCheckpoints();
        String[] classRoster = labSession.getClassRoster();
        boolean[][] checkpoints = labSession.getCheckpoints();
        ArrayList<String> labQueue = labSession.getLabQueue();

        return new LabState(sessionId, classRoster, checkpoints, labQueue);
    }

}
