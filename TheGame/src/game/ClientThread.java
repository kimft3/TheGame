package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread extends Thread {
	Socket serverSocket;

	String receiveString;
	String[] playerInfo;

	public ClientThread(Socket server) {
		this.serverSocket = server;
	}

	@Override
	public synchronized void run() {
		System.out.println("23456");

		while (true) {
			BufferedReader inFromServer;

			try {
				inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
				playerInfo = inFromServer.readLine().split("#");
				ClientGame.flytterundt(Integer.parseInt(playerInfo[2]), Integer.parseInt(playerInfo[3]),
						Integer.parseInt(playerInfo[4]), Integer.parseInt(playerInfo[5]), playerInfo[6]);
			} catch (IOException e) {
				e.printStackTrace();
			}

//		 		System.out.println(inFromServer.readLine());
// 				playerData=p.getName()+"#"+p.getPoint()+"#"+p.getXposOld()+"#"+p.getYposOld()+"#"+p.getXpos()+"#"+p.getYpos()+"#"+p.getDirection();
//				wait();

			try {
				crit(playerInfo);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("ClientThread RUN() Error: " + e);
				e.printStackTrace();
			}
			notify();
		}
	}

	public synchronized void crit(String[] playerInfo) {
		try {
			wait(10);
		} catch (InterruptedException e) {
			System.out.println("ClientThread CRIT() Error: " + e);
			e.printStackTrace();
		}
		ClientGame.updateScore(playerInfo[0], playerInfo[1]);
		notify();
	}

}
