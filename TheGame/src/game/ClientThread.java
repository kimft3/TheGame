package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread extends Thread {
	Socket serverSocket;

	String receiveString;
	String[] playerInfo;

	BufferedReader inFromServer;

	public ClientThread(Socket server) {
		this.serverSocket = server;
	}

	@Override
	public void run() {
		while (true) {
			try {
				inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
				receiveString = inFromServer.readLine();
				System.out.println("ct " + receiveString);
				playerInfo = receiveString.split("#");
				ClientGame.updateBoard(Integer.parseInt(playerInfo[2]), Integer.parseInt(playerInfo[3]),
						Integer.parseInt(playerInfo[4]), Integer.parseInt(playerInfo[5]), playerInfo[6]);
				ClientGame.updateScore(playerInfo[0], playerInfo[1]);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

}
