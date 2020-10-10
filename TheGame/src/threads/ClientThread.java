package threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import network_Game.ClientGame;

public class ClientThread extends Thread {
	Socket serverSocket;

	String receiveString;
	String[] playerInfo;

	BufferedReader inFromServer;

	public ClientThread(Socket server) {
		this.serverSocket = server;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		try {
			Thread.sleep(1000);
			inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		boolean play=true;
		while (play) {
			try {
				receiveString = inFromServer.readLine();
				playerInfo = receiveString.split("#");
				char action = playerInfo[0].charAt(0);
				switch (action) {
				case 'b':
					ClientGame.updateBoard(0, 0, Integer.parseInt(playerInfo[1]), Integer.parseInt(playerInfo[2]),
							"bomb");
					break;
				case 'e':
					ClientGame.updateBoard(0, 0, Integer.parseInt(playerInfo[1]), Integer.parseInt(playerInfo[2]),
							"explode");
					break;
				case 'w':
					ClientGame.updateBoard(0, 0, Integer.parseInt(playerInfo[1]), Integer.parseInt(playerInfo[2]),
							"floor");
					break;
				case 'q':
					ClientGame.updateBoard(0, 0, Integer.parseInt(playerInfo[2]), Integer.parseInt(playerInfo[3]),
							"floor");
					ClientGame.updateScore(playerInfo[1], null);
					break;
				default: //the last option a move from a player
					ClientGame.updateBoard(Integer.parseInt(playerInfo[3]), Integer.parseInt(playerInfo[4]),
							Integer.parseInt(playerInfo[5]), Integer.parseInt(playerInfo[6]), playerInfo[7]);
					ClientGame.updateScore(playerInfo[1], playerInfo[2]);
					break;

				}
			} catch (IOException e1) {
				 JFrame f=new JFrame();
				JOptionPane.showMessageDialog(f, "Ingen forbindelse til server");

				play=false;
				
			}
		}

	}

}
