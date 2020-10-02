package game;

import java.io.BufferedReader;
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
			try {
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
//		 System.out.println(inFromServer.readLine());
// playerData=p.getName()+"#"+p.getPoint()+"#"+p.getXposOld()+"#"+p.getYposOld()+"#"+p.getXpos()+"#"+p.getYpos()+"#"+p.getDirection();
				playerInfo = inFromServer.readLine().split("#");
				crit();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public synchronized void crit() throws InterruptedException {
		wait();
		ClientGame.flytterundt(Integer.parseInt(playerInfo[2]), Integer.parseInt(playerInfo[3]),
				Integer.parseInt(playerInfo[4]), Integer.parseInt(playerInfo[5]), playerInfo[6]);
		ClientGame.updateScore(playerInfo[0], playerInfo[1]);
		notify();
	}

}
