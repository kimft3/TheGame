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

	boolean nameValid = false;

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
//		if (receiveString.contains("Name is taken")) {
//			nameValid = false;
//			System.out.println("NameValid: " + nameValid);
//			ClientGame.invalidName();
//		} else {
//			nameValid = true;
//			System.out.println("NameValid: " + nameValid);
//			try {
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			try {
//				receiveString = inFromServer.readLine();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
	}

//		if (getNameValidation()) {
//			playerInfo = receiveString.split("#");
//			ClientGame.updateBoard(Integer.parseInt(playerInfo[2]), Integer.parseInt(playerInfo[3]),
//					Integer.parseInt(playerInfo[4]), Integer.parseInt(playerInfo[5]), playerInfo[6]);
//			ClientGame.updateScore(playerInfo[0], playerInfo[1]);
//		}
}
//
//	public boolean getNameValidation() {
//		boolean validated = false;
//		if (receiveString.contains("Name is taken")) {
//			nameValid = false;
//			System.out.println("NameValid: " + nameValid);
//		} else {
//			nameValid = true;
//			System.out.println("NameValid: " + nameValid);
//			validated = true;
//		}
//		return validated;
//	}
