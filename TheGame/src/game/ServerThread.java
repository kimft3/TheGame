package game;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;

import network.ServerGame;

public class ServerThread extends Thread {
	Socket serverReceiverSocket;

	String receiveString="",playerName="";;
		
	public ServerThread(Socket serverReceiverSocket) {
		this.serverReceiverSocket=serverReceiverSocket;}
	
	
	@Override
	public void run() {
		
		try {
			while(true) {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(serverReceiverSocket.getInputStream()));
			receiveString = inFromClient.readLine(); //join
			System.out.println("st "+receiveString);
			play();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
	}
	
	public void play() {
		System.out.println(receiveString);
		String[] playerMessage=receiveString.split("#");
		char id=playerMessage[0].charAt(0);
		switch(id) {
			case 'j':  					
				playerName=playerMessage[1];
				while(!ServerGame.isNameUnique(playerName)) {
					try {
						DataOutputStream outToClient = new DataOutputStream(serverReceiverSocket.getOutputStream());
						outToClient.writeBytes("Name is taken" + '\n' );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}			
				// Setting up standard players
				ServerGame.playerSockets.add(serverReceiverSocket);
					pair p=getRandomFreePosition();					
					ServerGame.players.add(new Player(playerName,p.getX(),p.getY(),"up"));
					System.out.println("opretter ");
					ServerGame.sendGameUpdate();
					break;
//			case m:
				
		}
		
	}
	public pair getRandomFreePosition()
	// finds a random new position which is not wall 
	// and not occupied by other players 
	{
		int x = 1;
		int y = 1;
		boolean found = false;
		while  (!found) {
			Random r = new Random();
			x = Math.abs(r.nextInt()%18) +1;
			y = Math.abs(r.nextInt()%18) +1;
			if (Generel.board[y].charAt(x)==' ')
			{
				found = true;
				for (Player p: ServerGame.players) {
					if (p.getXpos()==x && p.getYpos()==y)
						found = false;
				}
				
			}
		}
		pair p = new pair(x,y);
		return p;
	}
	
	
	
	
	

}
