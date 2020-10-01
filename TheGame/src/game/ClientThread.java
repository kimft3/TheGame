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

public void run() {
	System.out.println("23456");
	while(true) {
	try {
		 BufferedReader inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
//		 System.out.println(inFromServer.readLine());
		 ClientGame.updateGame(inFromServer.readLine());
	} catch (Exception e) {
		// TODO: handle exception
	}
}
}	

}
// Sende til clientGame