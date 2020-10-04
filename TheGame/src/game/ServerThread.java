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

	public ServerThread(Socket serverReceiverSocket) {
		this.serverReceiverSocket = serverReceiverSocket;
	}

	@Override
	public void run() {
		while (true) {
			try {

				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(serverReceiverSocket.getInputStream()));
				receiveString = inFromClient.readLine();
				System.out.println("st " + receiveString);
				DataOutputStream out = new DataOutputStream(serverReceiverSocket.getOutputStream());
				ServerGame.play(receiveString, out);

			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
