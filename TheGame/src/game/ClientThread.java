package game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread extends Thread {
	Socket serverSocket;

	String receiveString;

	public ClientThread(Socket server) {
		this.serverSocket = server;
	}

	@Override
	public void run() {
		System.out.println("23456");
		while (true) {
			try {
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
//		 System.out.println(inFromServer.readLine());
// playerData=p.getName()+"#"+p.getPoint()+"#"+p.getXposOld()+"#"+p.getYposOld()+"#"+p.getXpos()+"#"+p.getYpos()+"#"+p.getDirection();
				String[] playerInfo = inFromServer.readLine().split("#");
				ClientGame.flytterundt(Integer.parseInt(playerInfo[2]), Integer.parseInt(playerInfo[3]),
						Integer.parseInt(playerInfo[4]), Integer.parseInt(playerInfo[5]), playerInfo[6]);
//				 updateScore(String name, String score)
				ClientGame.updateScore(playerInfo[0], playerInfo[1]);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
