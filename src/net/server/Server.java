package net.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	ServerSocket MyService;
	Socket clientSocket;
	DataInputStream is;
	PrintStream os;



	public Server() {

		try {
			MyService = new ServerSocket(8888);
			
			clientSocket =  MyService.accept();
		} catch (IOException e) {
			System.out.println(e);
		}
	

		System.out.println("HELLO");
		server.start();
	}
	Thread server = new Thread(new Runnable() {
		public void run() {
			System.out.println("TEst");
			if(clientSocket == null ){
				System.out.println("ugh");
			}
			try {
				is = new DataInputStream(clientSocket.getInputStream());
				os = new PrintStream(clientSocket.getOutputStream());
			} catch (IOException e) {
				System.out.println(e);
			}
			try {
				System.out.println("HELLO2");
				System.out.println(is.readUTF());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	});
}
