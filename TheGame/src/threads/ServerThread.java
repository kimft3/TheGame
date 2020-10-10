package threads;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import network_Game.ServerGame;

public class ServerThread extends Thread {
	Socket serverReceiverSocket;
	String receiveString = "";
	BufferedReader inFromClient;
	DataOutputStream out;

	public ServerThread(Socket serverReceiverSocket) {
		this.serverReceiverSocket = serverReceiverSocket;
	}

	@Override
	public void run() {
		try {
			inFromClient = new BufferedReader(new InputStreamReader(serverReceiverSocket.getInputStream()));
			out = new DataOutputStream(serverReceiverSocket.getOutputStream());
		} catch (IOException e1) {
			System.out.println("her");
			e1.printStackTrace();
		}
		boolean play=true;
		while (play) {
			try {
				receiveString = inFromClient.readLine();
				
				ServerGame.play(receiveString, out);
			} catch (IOException | InterruptedException e) { // No connection, threads ends
		play=false;
				
			}
		}
	}
}
