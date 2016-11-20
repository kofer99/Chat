package gui.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.*;

/**
 * Lukas Franke
 */
public class ChatsClient
{
    public boolean IsConnected;
    public boolean IsHost;
    public Gui gui;
    public int ClientID;
    public String NickName = "Newbie";
    public String[] ConnectedNames;

    int port = 5123;
    MessageReceiver receiver;
    Socket socket;
    String host = "localhost";

    public ChatsClient(Gui gui)
    {
        this.gui = gui;
        try
        {
            print("Starting client...");
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));

            IsConnected = true;
            print("Done!");
            FetchClientID();
            receiver = new MessageReceiver(this);
            receiver.start();
        }
        catch (IOException e)
        {
            // TODO: Start a new server here if none exists yet

            IsConnected = false;
            e.printStackTrace();
        }
    }

    public ChatsClient(Gui gui, String nick)
    {
        this(gui);
        NickName = nick;
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

    public void Disconnect()
    {
        if (!IsConnected)
        {
            print("Already disconnected!");
            return;
        }

        IsConnected = false;

        // TODO
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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
    int fetchDelay;

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
                // Order a new list of client names around every second
                if (fetchDelay-- < 0)
                {
                    fetchDelay = 1000;
                    PrintWriter out = new PrintWriter(client.socket.getOutputStream(), false);

                    out.println("/fetchnames");
                    out.flush();

                    //client.print("Waiting for list of client names...");
                }

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
                        client.NickName = orders[2];
                }
                else if (orders[0].equals("/fetchnames"))
                {
                    String[] names = new String[orders.length - 1];
                    for (int i = 0; i < names.length; i++)
                        names[i] = orders[i + 1];

                    client.ConnectedNames = names;
                }
                else
                    client.OutputReceived(message);

                //client.print("Waiting for messages!");
            }
        }
        catch (SocketException e)
        {
            client.print("Client socket closed!");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
