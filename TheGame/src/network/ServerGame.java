package network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.Generel;
import game.Player;
import game.pair;
import javafx.scene.image.ImageView;

public class ServerGame {
	
	public static List<Player> players = new ArrayList<Player>();
	public static ArrayList<Socket> playerSockets=new ArrayList<>();
	

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		String receiveString="", sendString="", playerName="";
		ServerSocket talk=new ServerSocket(12345);
		BufferedReader replyText = new BufferedReader(new InputStreamReader(System.in));
		
		while (true) {
			System.out.println("hejsa");
			Socket connectionSocket = talk.accept();
			
			playerSockets.add(connectionSocket);
			
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());	
			
		
			
			
			receiveString = inFromClient.readLine(); //join 
			System.out.println(receiveString);
			String[] playerMessage=receiveString.split("#");
			char id=playerMessage[0].charAt(0);
			switch(id) {
				case 'j':  					
					playerName=playerMessage[1];
					while(!isNameUnique(playerName)) {
						outToClient.writeBytes("Name is taken, enter another name" + '\n');
						
					}			
					// Setting up standard players
						pair p=getRandomFreePosition();					
						players.add(new Player(playerName,p.getX(),p.getY(),"up"));
					System.out.println("opretter ");
					break;
//				case m:
					
			}
				
			
			
			
			
			
			// Unique name for all players
			
			
			
			
				System.out.println("S receiving "+receiveString);
				System.out.println("Server, Hvad vil du svare ");
			sendString = replyText.readLine() + '\n';
			System.out.println(" S sending "+sendString);
			
			outToClient.writeBytes(sendString);
	
		
		
		}
	}
	
	public static boolean isNameUnique(String name) {
		for(Player p:players) {
			if(p.getName().equals(name))
				return false;
		}
		return true;
	}
	public static pair getRandomFreePosition()
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
				for (Player p: players) {
					if (p.getXpos()==x && p.getYpos()==y)
						found = false;
				}
				
			}
		}
		pair p = new pair(x,y);
		return p;
	}
}
