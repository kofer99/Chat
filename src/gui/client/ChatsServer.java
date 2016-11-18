package gui.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Lukas Franke
 */
public class ChatsServer
{
    public ArrayList<Socket> Clients = new ArrayList<Socket>();
    public boolean IsRunning;
    public int ClientIDs;
    public ServerSocket HostSocket;

    ConnectionAccepter connAccepter;
    Hashtable<Integer, String> clientNicksByID = new Hashtable<Integer, String>();
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

    public void Stop()
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
                        out.println(m);
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

                    // TODO: We need to validate this as non-user input
                    if (message.startsWith("/clientID"))
                    {
                        // 1 is the client's NickName
                        String[] orders = message.split("\n");
                        server.print("Client expecting ID");

                        // TODO: This is probably not a good idea, or at least not good practice
                        int id = ++server.ClientIDs;
                        server.clientNicksByID.put(id, orders[1]);
                        server.print("Assigning ID " + id + " to nick '" + orders[1] + "'");

                        out.println("/clientID");
                        out.println(id);
                        out.flush();
                        server.print("Sent message!");
                    }
                    else if (message.startsWith("/nickname"))
                    {
                        // 1 is the ClientID, 2 the old Nickname, 3 the new one
                        String[] orders = message.split("\n");

                        if (server.clientNicksByID.containsValue(orders[3]))
                        {
                            out.println("/nickchange");
                            out.println("false");
                            out.flush();

                            server.print("Invalid nick change request by client " + orders[1] + " ('" + orders[2] + "' to '" + orders[3] + "').");
                        }
                        else
                        {
                            // substring to get rid of the \n
                            server.clientNicksByID.put(Integer.parseInt(orders[1].substring(0, 1)), orders[3]);
                            toOutput.add("'" + orders[2] + "' changed their name to '" + orders[3] + "'.");

                            out.println("/nickchange");
                            out.println("true");
                            out.println(orders[3]);
                            out.flush();

                            server.print("Changing nick of client " + orders[1] + " from '" + orders[2] + "' to '" + orders[3] + "'.");
                        }
                    }
                    else if (message.startsWith("/fetchnames"))
                    {
                        out.println("/fetchnames");
                        for (String n : server.clientNicksByID.values())
                            out.println(n);

                        out.flush();
                        server.print("Sending a list of client names.");
                    }
                    else if (message.equals("/disconnect"))
                    {
                        server.print("Client disconnecting. Sending goodby message...");

                        out.println("/disconnect");
                        out.println("Good bye!");
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
