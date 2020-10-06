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
//				if(!playerInfo[0].equals("b")) {
				ClientGame.updateBoard(Integer.parseInt(playerInfo[2]), Integer.parseInt(playerInfo[3]),
						Integer.parseInt(playerInfo[4]), Integer.parseInt(playerInfo[5]), playerInfo[6]);
				ClientGame.updateScore(playerInfo[0], playerInfo[1]);
//				}else {
//					
//					
//				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

}
