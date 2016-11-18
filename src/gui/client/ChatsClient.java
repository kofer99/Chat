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
            // TODO: Start a new server here if none exists yet

            e.printStackTrace();
        }
    }

    public void SetNickName(String newNick)
    {
        try
        {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), false);

            out.println("/nickname");
            out.println(ClientID);
            out.println(NickName);
            out.println(newNick);
            out.flush();

            print("Waiting for approval to change nickname from '" + NickName + "' to '" + newNick + "'...");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    void changeNick(String newNick)
    {
        NickName = newNick;
    }

    boolean hack;
    String[] names;
    public String[] FetchClientNames()
    {
        try
        {
            hack = true;
            PrintWriter out = new PrintWriter(socket.getOutputStream(), false);

            out.println("/fetchnames");
            //out.println(ClientID);
            out.flush();

            print("Waiting for list of client names...");

            // HACK: Ugh
            while (hack) { }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return Utils.RemoveBOM(names);
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

            out.println("[" + NickName + "] " + message);
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

            out.println("/clientID");
            out.println(NickName);
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

                String[] orders = Utils.SplitInput(message);

                if (orders[0].equals("/clientID"))
                {
                    client.ClientID = Integer.parseInt(orders[1]);
                    client.print("Got assigned to client ID: " + client.ClientID);
                }
                else if (orders[0].equals("/nickchange"))
                {
                    boolean consent = Boolean.parseBoolean(orders[1]);
                    client.print("Got consent for nickname change: " + consent);
                    if (consent)
                        client.changeNick(orders[2]);
                }
                else if (orders[0].equals("/fetchnames"))
                {
                    String[] names = new String[orders.length - 1];
                    for (int i = 0; i < names.length; i++)
                        names[i] = orders[i + 1];

                    client.hack = false;
                    client.names = names;
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
