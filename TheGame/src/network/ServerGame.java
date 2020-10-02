package network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.Generel;
import game.Player;
import game.pair;
import game.ServerThread;
public class ServerGame {
	
	public static List<Player> players = new ArrayList<Player>();
	public static ArrayList<Socket> playerSockets=new ArrayList<>(); // evt ændre til DataOutputStream-------------------------
	

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		String receiveString="", sendString="", playerName="";
		ServerSocket talk=new ServerSocket(12345);
		while (true) {
			Socket connectionSocket = talk.accept();
			(new ServerThread(connectionSocket)).start();
			
			
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());	
			
		
		}
	}
	
	public static void sendGameUpdate() {
		for(Socket s: playerSockets) {
			try {
				DataOutputStream outToClient = new DataOutputStream(s.getOutputStream());
				String playerData="";
				for(Player p:players) {
					playerData=p.getName()+"#"+p.getPoint()+"#"+p.getXposOld()+"#"+p.getYposOld()+"#"+p.getXpos()+"#"+p.getYpos()+"#"+p.getDirection();
					System.out.println("S "+playerData+'\n');
					outToClient.writeBytes(playerData+'\n');
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public static boolean isNameUnique(String name) {
		for(Player p:players) {
			if(p.getName().equals(name))				
				return false;		}
		return true;
	}
	
}
