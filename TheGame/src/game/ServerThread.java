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
		System.out.println("ServerThread "+receiveString);
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
			case 'm':
				for(Player pl: ServerGame.players) {
					if(pl.getName().equals(playerMessage[1])) {
						updatePlayer(pl,Integer.parseInt(playerMessage[2]),Integer.parseInt(playerMessage[3]),playerMessage[4]);	
					}
				}
				
				
				
				break;
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
	
	
	public void updatePlayer(Player me,int delta_x, int delta_y, String direction)
	{
		
		me.direction = direction;
		int x = me.getXpos(),y = me.getYpos();

		if (Generel.board[y+delta_y].charAt(x+delta_x)=='w') {
			me.addPoints(-1);
		} 
		else {
			// prepared for collision detection
			// not quite relevant in single plaver version		
			Player p = getPlayerAt(x+delta_x,y+delta_y);
			if (p!=null) {
              me.addPoints(10);
              //update the other player
              p.addPoints(-10);
              pair pa = getRandomFreePosition();
              p.xpos=pa.getX();
              p.ypos=pa.getY();
 //             movePlayerOnScreen(x+delta_x,y+delta_y,pa.getX(),pa.getY(),p.direction);
			} else 
				me.addPoints(1);
//			movePlayerOnScreen(x,y,x+delta_x,y+delta_y,direction);
			me.setXpos(x+delta_x);
			me.setYpos(y+delta_y);
			ServerGame.sendGameUpdate();
		}
		
	}
	
	public Player getPlayerAt(int x, int y) {
		for (Player p : ServerGame.players) {
			if (p.getXpos()==x && p.getYpos()==y) {
				return p;
			}
		}
		return null;
	}
	
	
	

}
