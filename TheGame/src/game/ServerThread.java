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
			ServerGame.play(receiveString,serverReceiverSocket);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
	}

}
