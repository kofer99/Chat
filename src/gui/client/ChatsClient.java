package gui.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;

/**
 * Lukas Franke
 */
public class ChatsClient
{
	public Gui gui ;
	
    public int ClientID;
    public String NickName;

    int port = 5123;
    MessageReceiver receiver;
    Socket socket;
    String host = "localhost";

    public ChatsClient(String nickname, Gui gui)
    {
    	this.gui = gui;
        NickName = nickname;
        try
        {
            print("Starting client...");
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));

            print("Done!");
            FetchClientID();
            receiver = new MessageReceiver(this);
            receiver.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
    
            
        }
    }



    public boolean SetNickName(String newNick)
    {
        NickName = newNick;

        // TODO: The server needs to validate this
        return true;
    }
    public String getNickName(){
    	return NickName;
    }

    // Make this a bool as well?
    public void SendMessage(String message)
    {
        if (socket.isClosed())
        {
            print("Socket closed. Can't send \"" + message + "\"!");
            return;
        }
        
        try
        {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), false);

            out.print("[" + NickName + "] " + message);
            out.flush();

            print("Sent message \"" + message + "\"!");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    void OutputReceived(String message)
    {
        print("Output: " + message);
        gui.printMessage(message);
    }

    void FetchClientID()
    {
        try
        {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), false);

            out.print("Expecting Client ID");
            out.flush();

            print("Waiting for a client ID...");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    void print(String s)
    {
        System.out.println("[Client] " + s);
    }
}

class MessageReceiver extends Thread
{
    ChatsClient client;
    boolean firstMessage = true;

    public MessageReceiver(ChatsClient client)
    {
        this.client = client;
    }

    public void run()
    {
        try
        {
            while (!client.socket.isClosed())
            {
                InputStream in = client.socket.getInputStream();
                //client.print("Checking own Input Stream for messages...");

                String message = Utils.TextFromInputstream(in);
                if (message.isEmpty())
                {
                    //client.print("Nothing received!");
                    continue;
                }

                client.print("Received: " + message);

                // "HACK": Ugh
                // The first message should be the client ID
                if (firstMessage)
                {
                    firstMessage = false;
                    client.ClientID = Integer.parseInt(message);
                    client.print("Got assigned to client ID: " + client.ClientID);
                }
                else
                    client.OutputReceived(message);

                client.print("Waiting for messages!");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
