package gui.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Lukas Franke
 */
public class ChatsServer
{
    public static ArrayList<Socket> Clients = new ArrayList<Socket>();
    public static boolean IsRunning;
    public int ClientIDs;
    public ServerSocket HostSocket;

    ConnectionAccepter connAccepter;
    MainLoop looper;
    int port = 5123;
    String host = "localhost";

    public ChatsServer()
    {
        try
        {
            HostSocket = new ServerSocket(port);
            IsRunning = true;

            connAccepter = new ConnectionAccepter(this);
            looper = new MainLoop(this);

            connAccepter.start();
            looper.start();

            print("Server gestartet!");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void Stop()
    {
        try
        {
            IsRunning = false;

            // .stop() is deprecated?!
            // connAccepter.stop();
            // looper.stop();

            for (Socket s : Clients)
                s.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } 
    }

    void print(String s)
    {
        System.out.println("[Server] " + s);
    }
}

class MainLoop extends Thread
{
    ChatsServer server;
    ArrayList<Socket> markedForRemoval = new ArrayList<Socket>();
    ArrayList<String> toOutput = new ArrayList<String>();
    public MainLoop(ChatsServer server)
    {
        this.server = server;
    }

    public void run()
    {
        try
        {
            while (server.IsRunning)
            {
                for (Socket s : markedForRemoval)
                    server.Clients.remove(s);

                ArrayList<String> currentOutput = new ArrayList<String>(toOutput);
                toOutput.clear();
                    
                // Nicht enumerieren waehrend die Liste veraendert wird
                ArrayList<Socket> sockets = new ArrayList<Socket>(server.Clients);
                for (Socket s : sockets)
                {
                    if (s.isClosed())
                    {
                        if (!markedForRemoval.contains(s))
                            markedForRemoval.add(s);

                        continue;
                    }

                    PrintWriter out = new PrintWriter(s.getOutputStream(), false);

                    // Send all old received messages beforehand
                    for (String m : currentOutput)
                    {
                        out.print(m);
                        out.flush();
                    }

                    InputStream in = s.getInputStream();
                    //server.print("Input stream received. Checking for messages...");

                    String message = Utils.TextFromInputstream(in);
                    if (message.isEmpty())
                    {
                        //server.print("Nothing received!");
                        continue;
                    }

                    server.print("Received: " + message);

                    if (message.equals("Expecting Client ID"))
                    {
                        server.print("Client expecting ID");

                        // TODO: This is probably not a good idea, or at least not good practice
                        server.ClientIDs++;
                        int id = server.ClientIDs;
                        server.print("Assigning ID " + id);

                        out.print(id);
                        out.flush();
                        server.print("Sent message!");
                    }
                    else if (message.equals("Disconnect"))
                    {
                        server.print("Client disconnecting. Sending goodby message...");

                        out.print("Good bye!");
                        out.flush();

                        server.print("Closing connection!");
                        s.close();
                        server.print("Sent message!");
                    }
                    else
                    {
                        // TODO: Exclude own client
                        server.print("Received input from client.");
                        toOutput.add(message);
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

class ConnectionAccepter extends Thread
{
    ChatsServer server;
    public ConnectionAccepter(ChatsServer server)
    {
        this.server = server;
    }

    public void run()
    {
        try
        {
            while (server.IsRunning)
            {
                Socket socket = server.HostSocket.accept();
                server.Clients.add(socket);
                server.print("Server accepted connection");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
