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
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			try {
				receiveString = inFromServer.readLine();
				System.out.println("ct" + receiveString);
				playerInfo = receiveString.split("#");
				char action =playerInfo[0].charAt(0);
				switch(action) {
				case 'b':
					ClientGame.updateBoard(0, 0,
							Integer.parseInt(playerInfo[1]), Integer.parseInt(playerInfo[2]), "bomb");
				break;
				case 'e':
					ClientGame.updateBoard(0, 0,
							Integer.parseInt(playerInfo[1]), Integer.parseInt(playerInfo[2]), "explode");
				break;
				case 'w':
					ClientGame.updateBoard(0, 0,
							Integer.parseInt(playerInfo[1]), Integer.parseInt(playerInfo[2]), "floor");
					break;
				default:			
					ClientGame.updateBoard(Integer.parseInt(playerInfo[2]), Integer.parseInt(playerInfo[3]),
							Integer.parseInt(playerInfo[4]), Integer.parseInt(playerInfo[5]), playerInfo[6]);
					ClientGame.updateScore(playerInfo[0], playerInfo[1]);
					break;
					
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

}
