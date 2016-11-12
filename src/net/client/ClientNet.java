package net.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;



public class ClientNet{
	Socket cClient;
    DataOutputStream os = null;
    DataInputStream is = null;
    String host = "localhost";
    InetAddress address; 

	
	public ClientNet() {
		try {
			address = InetAddress.getByName(host);
			cClient = new Socket(address ,8888);
			os = new DataOutputStream(cClient.getOutputStream());
			is = new DataInputStream(cClient.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ClientR");
		//client.start();
	}
	
	public void SendMessage(){
		try {
			os.writeChars("hi");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	Thread client = new Thread(new Runnable() {
		public void run() {
		    if (cClient != null && os != null && is != null) {
	         
			try {
				os.writeChars("hi");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}}
	});


}
