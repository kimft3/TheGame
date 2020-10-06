package game;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import network.ServerGame;

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
			e1.printStackTrace();
		}
		while (true) {
			try {
				receiveString = inFromClient.readLine();
				System.out.println("st " + receiveString);

				ServerGame.play(receiveString, out);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
