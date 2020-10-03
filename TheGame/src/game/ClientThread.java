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
	public synchronized void run() {
		System.out.println("23456");

		try {
			inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			if (inFromServer.readLine().contains("Name is taken")) {
				System.out.println("false");
				ClientGame.nameValid = false;
				ClientGame.invalidName();
			} else {
				System.out.println("true");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (true) {
//			BufferedReader inFromServer;
			try {
//				inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

				playerInfo = inFromServer.readLine().split("#");
				ClientGame.updateBoard(Integer.parseInt(playerInfo[2]), Integer.parseInt(playerInfo[3]),
						Integer.parseInt(playerInfo[4]), Integer.parseInt(playerInfo[5]), playerInfo[6]);
				ClientGame.updateScore(playerInfo[0], playerInfo[1]);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
